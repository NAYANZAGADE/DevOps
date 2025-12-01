#!/usr/bin/env python3
import weasyprint

# Convert HTML to PDF
html_file = '/home/nayan/workspace/portifolio/resume.html'
pdf_file = '/home/nayan/workspace/portifolio/Nayan_Zagade_Resume.pdf'

weasyprint.HTML(filename=html_file).write_pdf(pdf_file)
print(f"PDF saved as: {pdf_file}")
