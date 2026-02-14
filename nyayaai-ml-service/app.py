from fastapi import FastAPI
from pydantic import BaseModel
import torch
import pandas as pd
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI(title="NyayaAI ML Service")

# Load everything ONCE at startup
model = SentenceTransformer("legal_section_model")
section_embeddings = torch.load("section_embeddings.pt")
df = pd.read_csv("sections_df.csv")

class ComplaintRequest(BaseModel):
    complaint: str
    top_k: int = 3

def confidence(score):
    if score >= 0.55:
        return "High"
    elif score >= 0.40:
        return "Medium"
    return "Low"

@app.post("/predict")
def predict_sections(req: ComplaintRequest):
    emb = model.encode(req.complaint, convert_to_tensor=True)

    sims = cosine_similarity(
        emb.cpu().numpy().reshape(1, -1),
        section_embeddings.cpu().numpy()
    )[0]

    top_idx = np.argsort(sims)[-req.top_k:][::-1]

    results = []
    for i in top_idx:
        score = float(sims[i])
        results.append({
            "section": str(df.iloc[i]["Section"]),
            "section_name": df.iloc[i]["Section _name"],
            "similarity": round(score, 4),
            "confidence": confidence(score),
            "description": df.iloc[i]["Description"]
        })

    return results


import json
from fastapi.responses import StreamingResponse
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from io import BytesIO
from typing import Dict, Any, Union, List

def format_custom_date(date_obj: Union[List[int], str]) -> str:
    """
    Converts [2026, 2, 13] -> "13th Feb 2026"
    """
    if isinstance(date_obj, list) and len(date_obj) == 3:
        y, m, d = date_obj
        months = ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
        
        # Determine suffix (st, nd, rd, th)
        if 11 <= d <= 13:
            suffix = "th"
        else:
            suffix = {1: "st", 2: "nd", 3: "rd"}.get(d % 10, "th")
            
        month_name = months[m] if 1 <= m <= 12 else str(m)
        return f"{d}{suffix} {month_name} {y}"
    return str(date_obj)

def format_custom_time(time_obj: Union[List[int], str]) -> str:
    """
    Converts [22, 21] -> "22 : 21"
    """
    if isinstance(time_obj, list) and len(time_obj) >= 2:
        h, m = time_obj[0], time_obj[1]
        return f"{h:02d} : {m:02d}"
    return str(time_obj)

@app.post("/generate-fir-pdf")
async def generate_fir_pdf(data: Dict[str, Any]):
    buffer = BytesIO()
    
    # Setup Document
    doc = SimpleDocTemplate(
        buffer, 
        pagesize=A4,
        rightMargin=40, leftMargin=40, 
        topMargin=40, bottomMargin=40
    )
    elements = []

    # =========================
    # STYLES
    # =========================
    styles = getSampleStyleSheet()
    
    style_center_bold = ParagraphStyle(
        name="CenterBold",
        parent=styles["Heading1"],
        alignment=TA_CENTER,
        fontName="Helvetica-Bold",
        fontSize=14,
        spaceAfter=2
    )
    
    style_center_sub = ParagraphStyle(
        name="CenterSub",
        parent=styles["Normal"],
        alignment=TA_CENTER,
        fontName="Helvetica-Bold",
        fontSize=10,
        spaceAfter=20
    )

    style_normal = ParagraphStyle(
        name="CustomNormal",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=10,
        leading=14
    )

    style_bold = ParagraphStyle(
        name="CustomBold",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=10,
        leading=14
    )

    # =========================
    # PRE-PROCESSING DATA
    # =========================
    # 1. Format Dates and Times
    incident_date_raw = data.get('incidentDate')
    incident_time_raw = data.get('incidentTime')
    
    formatted_date = format_custom_date(incident_date_raw)
    formatted_time = format_custom_time(incident_time_raw)
    
    # 2. Format Year (if it comes as a list or raw string)
    year_val = data.get('year', '')
    if isinstance(year_val, list):
        year_val = year_val[0]

    district_val = data.get('district', '')
    ps_val = data.get('policeStation', '')
    fir_id = data.get('id', '')

    # =========================
    # 1. HEADER
    # =========================
    elements.append(Paragraph("FIRST INFORMATION REPORT", style_center_bold))
    elements.append(Paragraph("(under section 154 Cr.P.C.)", style_center_sub))

    # =========================
    # 2. DISTRICT & FIR DETAILS
    # =========================
    header_data = [
        [
            Paragraph(f"<b>1. District.</b> ( {district_val} )", style_normal),
            Paragraph(f"<b>P.S:</b> {ps_val}", style_normal)
        ],
        [
            Paragraph(f"<b>F.I.R No :</b> {fir_id}", style_normal),
            Paragraph(f"<b>Year :</b> ( {year_val} )", style_normal)
        ],
        [
            # Combined Date and Time line
            Paragraph(f"<b>Date and Time of F.I.R:</b> ( {formatted_date} ) {formatted_time}", style_normal),
            "" 
        ]
    ]

    header_table = Table(header_data, colWidths=[300, 200])
    header_table.setStyle(TableStyle([
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('LEFTPADDING', (0,0), (-1,-1), 0),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
    ]))
    elements.append(header_table)
    elements.append(Spacer(1, 10))

    # =========================
    # 3. ACTS AND SECTIONS (With IPC -> BNS Logic)
    # =========================
    elements.append(Paragraph("2.", style_bold))
    
    # Table Header
    acts_table_data = [[
        Paragraph("<b>S. No</b>", style_normal),
        Paragraph("<b>Acts</b>", style_normal),
        Paragraph("<b>Section</b>", style_normal)
    ]]

    # Parse Sections
    raw_sections = data.get("predictedSections", "[]")
    try:
        if isinstance(raw_sections, str):
            sections_list = json.loads(raw_sections)
        else:
            sections_list = raw_sections
    except Exception:
        sections_list = []

    if sections_list:
        for idx, item in enumerate(sections_list, 1):
            act_name = item.get("category", "BNS") # Default to BNS if missing
            sec_num = item.get("sectionNumber", "")
            
            # --- LOGIC: Replace IPC with BNS ---
            if "IPC" in act_name.upper():
                act_name = act_name.replace("IPC", "BNS").replace("ipc", "BNS")
            
            row = [
                str(idx),
                Paragraph(act_name, style_normal),
                Paragraph(sec_num, style_normal)
            ]
            acts_table_data.append(row)
    else:
        acts_table_data.append(["-", "-", "-"])

    t = Table(acts_table_data, colWidths=[40, 300, 150])
    t.setStyle(TableStyle([
        ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 6),
    ]))
    elements.append(t)
    elements.append(Spacer(1, 15))

    # =========================
    # 4. OCCURRENCE OF OFFENCE
    # =========================
    elements.append(Paragraph("2. (A) Occurrence of offence", style_bold))
    
    day_val = data.get('day', '')
    
    # Use formatted time for Occurrence time as well if applicable
    # (Assuming user might send same format for occurrence time)
    
    occurrence_data = [
        [
            Paragraph(f"<b>1. Day</b> ( {day_val} )", style_normal),
            Paragraph(f"<b>Time period :</b> {formatted_time}", style_normal)
        ]
    ]
    
    occ_table = Table(occurrence_data, colWidths=[300, 200])
    occ_table.setStyle(TableStyle([
        ('LEFTPADDING', (0,0), (-1,-1), 0),
    ]))
    elements.append(occ_table)

    elements.append(Paragraph(f"<b>(B) Information received P.S:</b> ( {ps_val} )", style_normal))
    elements.append(Spacer(1, 10))

    # =========================
    # 5. COMPLAINANT DETAILS
    # =========================
    comp_name = data.get('complainantName', '')
    relation = data.get('relationToVictim', '')
    occupation = data.get('occupation', '')
    address = data.get('address', '')

    if relation:
        comp_text = f"<b>Complainant Name :</b> {comp_name} ({relation})"
    else:
        comp_text = f"<b>Complainant Name :</b> {comp_name}"
    elements.append(Paragraph(comp_text, style_normal))
    
    comp_details_data = [
        [
            Paragraph(f"<b>Occupation :</b> {occupation}", style_normal),
            Paragraph(f"<b>Address :</b> {address}", style_normal)
        ]
    ]
    
    comp_table = Table(comp_details_data, colWidths=[200, 300])
    comp_table.setStyle(TableStyle([
        ('LEFTPADDING', (0,0), (-1,-1), 0),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
    ]))
    elements.append(comp_table)
    elements.append(Spacer(1, 10))

    # =========================
    # 6. ACCUSED DETAILS
    # =========================
    elements.append(Paragraph("Details of known Accused/Suspect.", style_normal))
    
    accused_list = data.get("accusedList", [])
    if not accused_list:
        accused_list = [{"name": "Unknown", "address": ""}]

    accused_table_data = []
    for idx, acc in enumerate(accused_list, 1):
        row = [
            str(idx) + ".",
            Paragraph(f"<b>Name: {acc.get('name', '')}</b>", style_normal),
            Paragraph(f"<b>A/p.</b> {acc.get('address', '')}", style_normal)
        ]
        accused_table_data.append(row)

    acc_table = Table(accused_table_data, colWidths=[30, 200, 270])
    acc_table.setStyle(TableStyle([
        ('GRID', (0,0), (-1,-1), 0.5, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 5),
    ]))
    elements.append(acc_table)
    elements.append(Spacer(1, 10))
    
    elements.append(Paragraph(f"<b>Place of Occurrence:</b> {data.get('placeOfOccurrence', '')}", style_normal))
    elements.append(Spacer(1, 15))

    # =========================
    # 7. COMPLAINT CONTENTS
    # =========================
    style_justify = ParagraphStyle(
        name="Justify",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=10,
        leading=14,
        alignment=TA_JUSTIFY
    )

    elements.append(Paragraph(f"<b>First Information contents ( {formatted_date} ):</b>", style_normal))
    elements.append(Paragraph(data.get("complaintText", ""), style_justify))

    # Build PDF
    doc.build(elements)
    buffer.seek(0)

    return StreamingResponse(
        buffer,
        media_type="application/pdf",
        headers={
            "Content-Disposition": f"attachment; filename=FIR_{fir_id}.pdf"
        }
    )