#!/usr/bin/env python3
import weasyprint

# Convert ATS HTML to PDF
html_file = '/home/nayan/workspace/portifolio/resume_ats.html'
pdf_file = '/home/nayan/workspace/portifolio/Nayan_Zagade_Resume_ATS.pdf'

weasyprint.HTML(filename=html_file).write_pdf(pdf_file)
print(f"ATS-optimized PDF saved as: {pdf_file}")
