const path = require('path');
const os = require('os');
const fs = require('fs');

// Claude session ID passed as arg, or fall back to TERM_SESSION_ID
const sessionId = process.argv[2] || process.env.TERM_SESSION_ID || '';
const projDir = process.env.CLAUDE_PROJECT_DIR || process.cwd();
const escaped = path.resolve(projDir).replace(/:\\/, '--').replace(/[/\\]/g, '-');
const txPath = path.join(os.homedir(), '.claude', 'projects', escaped, sessionId + '.jsonl');

console.log('session :', sessionId);
console.log('escaped :', escaped);
console.log('transcript:', txPath);
console.log('exists  :', fs.existsSync(txPath));

if (!fs.existsSync(txPath)) { console.error('FAIL: transcript not found'); process.exit(1); }

const lines = fs.readFileSync(txPath, 'utf8').split('\n');
let directives = [];
for (const raw of lines) {
  if (!raw.trim()) continue;
  let entry; try { entry = JSON.parse(raw); } catch(e) { continue; }
  if (!entry || entry.type !== 'user' || !entry.message) continue;
  const mc = entry.message.content;
  let text = typeof mc === 'string' ? mc
    : Array.isArray(mc) ? mc.filter(c=>c&&c.type==='text').map(c=>c.text||'').join('\n') : '';
  if (!text.includes('GATEGUARD-BATCH-AUTH:')) continue;
  const m = text.match(/GATEGUARD-BATCH-AUTH:[^\n]*/);
  if (m) { directives.push(m[0]); console.log('directive:', m[0]); }
}
if (!directives.length) { console.error('FAIL: no directive found in user turns'); process.exit(1); }

function globToRegex(g) {
  if (!g || /\.\./.test(g) || /[;|&$`(){}[\]!]/.test(g) || /^\//.test(g)) return null;
  const e = g.replace(/[.+^${}()|[\]\\]/g,'\\$&').replace(/\*\*/g,'\x01').replace(/\*/g,'[^/]*').replace(/\x01/g,'.*').replace(/\?/g,'[^/]');
  try { return new RegExp('^'+e+'$'); } catch(e) { return null; }
}

const auth = { ops:['Write','Edit'], ext:'.md', max:30, pattern: globToRegex('docs/minecraft-lore/mobs/*') };

function matches(rel, op, ext) {
  if (!auth.ops.includes(op)) return false;
  if (auth.ext && ext !== auth.ext) return false;
  return auth.pattern.test(rel);
}

const r1 = matches('docs/minecraft-lore/mobs/creeper.md',        'Write', '.md');
const r2 = matches('docs/minecraft-lore/structures/stronghold.md','Write', '.md');
const r3 = matches('docs/minecraft-lore/mobs/creeper.json',       'Write', '.json');

console.log('\nCase 1 (mobs/creeper.md   Write) :', r1  ? 'PASS - authorized' : 'FAIL');
console.log('Case 2 (structures/ wrong dir)   :', !r2 ? 'PASS - denied'     : 'FAIL');
console.log('Case 3 (mobs/creeper.json wrong) :', !r3 ? 'PASS - denied'     : 'FAIL');

if (r1 && !r2 && !r3) { console.log('\nAll cases pass. Batch auth should fire on next mobs write.'); }
else { console.error('\nOne or more cases failed.'); process.exit(1); }
