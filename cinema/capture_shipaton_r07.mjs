import { chromium } from 'playwright';

const wait=ms=>new Promise(r=>setTimeout(r,ms));
const browser=await chromium.launch({
  headless:false,
  args:['--no-sandbox','--disable-dev-shm-usage','--kiosk','--window-size=1280,720']
});
const page=await browser.newPage({viewport:{width:1280,height:720}});
await page.goto('http://127.0.0.1:8080/cinema/',{waitUntil:'networkidle'});
await wait(1200);

async function glide(x,y,steps=24){ await page.mouse.move(x,y,{steps}); }

await page.evaluate(()=>document.querySelector('#intro').classList.add('show'));
await wait(3400);
await page.evaluate(()=>document.querySelector('#intro').classList.remove('show'));
await wait(900);

await glide(500,614); await page.mouse.click(500,614); await wait(16000);

await glide(120,300); await page.mouse.click(120,300); await wait(900);
await glide(520,614); await page.mouse.click(520,614); await wait(16000);

await glide(120,385); await page.mouse.click(120,385); await wait(900);
await glide(500,556); await page.mouse.click(500,556); await wait(18000);

await glide(120,475); await page.mouse.click(120,475); await wait(900);
await glide(505,500); await page.mouse.click(505,500); await wait(13000);

await glide(120,210); await page.mouse.click(120,210); await wait(1000);
await glide(500,614); await wait(5500);
await glide(120,300); await wait(3500);
await glide(120,385); await wait(3500);
await glide(120,475); await wait(3500);

await page.evaluate(()=>document.querySelector('#intro').classList.add('show'));
await wait(6500);
await browser.close();
