import fs from 'node:fs';
import {execFileSync} from 'node:child_process';

const file=process.argv[2];
if(!file) throw new Error('usage: node qa.mjs <video>');
const raw=execFileSync('ffprobe',['-v','error','-show_entries','format=duration,size','-show_entries','stream=codec_type,codec_name,width,height,avg_frame_rate','-of','json',file],{encoding:'utf8'});
const p=JSON.parse(raw);
const video=p.streams.find(s=>s.codec_type==='video');
const audio=p.streams.find(s=>s.codec_type==='audio');
const dur=Number(p.format.duration);
const fps=video?.avg_frame_rate?.split('/').reduce((a,b)=>Number(a)/Number(b));
const checks={
  duration_under_120: dur>0 && dur<120,
  width_1920: video?.width===1920,
  height_1080: video?.height===1080,
  fps_60: Math.abs((fps||0)-60)<0.05,
  h264: video?.codec_name==='h264',
  audio_present: Boolean(audio),
  nonzero_size: Number(p.format.size)>1000000
};
const pass=Object.values(checks).every(Boolean);
const out={schema:1,file,duration:dur,fps,checks,status:pass?'PASS':'FAIL'};
fs.mkdirSync('studio-os/evidence',{recursive:true});
fs.writeFileSync('studio-os/evidence/FINAL_MASTER_QA.json',JSON.stringify(out,null,2));
console.log(JSON.stringify(out,null,2));
if(!pass) process.exit(1);
