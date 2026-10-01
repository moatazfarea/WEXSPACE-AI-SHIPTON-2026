import fs from 'node:fs';

const manifestPath=process.argv[2] || 'studio-os/production/SHIPATON_FILM_R04.json';
const m=JSON.parse(fs.readFileSync(manifestPath,'utf8'));
const errors=[];
if(m.target_duration_seconds>m.max_duration_seconds) errors.push('target duration exceeds max');
if(!m.rules.real_runtime_required) errors.push('real runtime must be required');
if(!m.rules.slide_deck_forbidden) errors.push('slide deck must be forbidden');
if(!m.rules.simulated_success_forbidden) errors.push('simulated success must be forbidden');

const required=['REQUEST','SPECIALIST','TOOL','VERIFY','EVIDENCE'];
for(const p of m.projects){
  for(const step of required) if(!p.required_steps.includes(step)) errors.push(p.id+' missing '+step);
}
let last=0;
for(const b of m.beats){
  if(b.start<last) errors.push('overlapping/out-of-order beat at '+b.start);
  if(b.end<=b.start) errors.push('invalid beat '+JSON.stringify(b));
  if(b.truth==='FORBIDDEN_SIMULATION') errors.push('forbidden truth class present');
  last=b.end;
}
if(last>m.max_duration_seconds) errors.push('beats exceed max duration');

const receipt={
  schema:1,
  production_id:m.production_id,
  project_count:m.projects.length,
  beat_count:m.beats.length,
  runtime_seconds:last,
  required_execution_grammar:required,
  status:errors.length?'FAIL':'PASS',
  errors
};
fs.mkdirSync('studio-os/evidence',{recursive:true});
fs.writeFileSync('studio-os/evidence/PRODUCTION_MANIFEST_QUALIFICATION.json',JSON.stringify(receipt,null,2));
console.log(JSON.stringify(receipt,null,2));
if(errors.length) process.exit(1);
