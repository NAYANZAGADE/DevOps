const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

async function generatePDF() {
    const browser = await puppeteer.launch({
        headless: 'new',
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    const page = await browser.newPage();
    const htmlPath = path.join(__dirname, 'resume.html');
    const htmlContent = fs.readFileSync(htmlPath, 'utf8');
    
    await page.setContent(htmlContent, { waitUntil: 'networkidle0' });
    
    await page.pdf({
        path: 'Nayan_Zagade_Resume.pdf',
        format: 'A4',
        printBackground: true,
        margin: {
            top: '0.5in',
            right: '0.5in',
            bottom: '0.5in',
            left: '0.5in'
        }
    });
    
    await browser.close();
    console.log('PDF generated successfully: Nayan_Zagade_Resume.pdf');
}

generatePDF().catch(console.error);
