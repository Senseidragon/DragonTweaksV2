#!/usr/bin/env node
'use strict';

const childProcess = require('child_process');
const fs = require('fs');
const os = require('os');
const path = require('path');

const DEFAULT_VERSION = '21.1.230';
const DEFAULT_OUT_DIR = path.join('docs', 'api', 'neoforge');
const DEFAULT_INDEX = path.join('docs', 'NEOFORGE_API_INDEX.md');

function parseArgs(argv) {
  const args = {
    version: DEFAULT_VERSION,
    jar: '',
    outDir: DEFAULT_OUT_DIR,
    index: DEFAULT_INDEX,
    dryRun: false,
    limit: 0,
    noExtract: false,
    include: '',
    apiName: '',
    sourceDesc: '',
  };

  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];
    if (arg === '--dry-run') args.dryRun = true;
    else if (arg === '--no-extract') args.noExtract = true;
    else if (arg === '--version') args.version = requireValue(argv, ++i, arg);
    else if (arg === '--jar') args.jar = requireValue(argv, ++i, arg);
    else if (arg === '--out-dir') args.outDir = requireValue(argv, ++i, arg);
    else if (arg === '--index') args.index = requireValue(argv, ++i, arg);
    else if (arg === '--limit') args.limit = Number(requireValue(argv, ++i, arg)) || 0;
    else if (arg === '--include') args.include = normalizeIncludePrefix(requireValue(argv, ++i, arg));
    else if (arg === '--api-name') args.apiName = requireValue(argv, ++i, arg);
    else if (arg === '--source-desc') args.sourceDesc = requireValue(argv, ++i, arg);
    else if (arg === '--help' || arg === '-h') {
      printHelp();
      process.exit(0);
    } else {
      throw new Error(`Unknown argument: ${arg}`);
    }
  }

  return args;
}

function requireValue(argv, index, flag) {
  if (index >= argv.length || argv[index].startsWith('--')) {
    throw new Error(`${flag} requires a value`);
  }
  return argv[index];
}

function printHelp() {
  console.log([
    'Usage: node tools/rebuild-neoforge-api-index.js [options]',
    '',
    'Options:',
    '  --dry-run              Parse and report without writing files or deleting output',
    '  --limit N              Process only the first N Java files for quick testing',
    '  --include PREFIX       Process only Java files under a source path prefix, such as fluids/',
    `  --version VERSION      API version string used in index header (default: ${DEFAULT_VERSION})`,
    '  --jar PATH             Explicit sources jar path (JAR extraction mode only)',
    `  --out-dir PATH         Extracted source reference directory (default: ${DEFAULT_OUT_DIR})`,
    `  --index PATH           Generated index path (default: ${DEFAULT_INDEX})`,
    '  --no-extract           Generate index from a previously extracted output directory (no JAR needed)',
    '  --api-name NAME        API name used in index title and header (default: NeoForge)',
    '  --source-desc DESC     Source description line in index header (default: jar path or out-dir)',
  ].join('\n'));
}

function run(command, args, options = {}) {
  const result = childProcess.spawnSync(command, args, {
    encoding: 'utf8',
    ...options,
  });
  if (result.error) throw result.error;
  if (result.status !== 0) {
    throw new Error(`${command} ${args.join(' ')} failed:\n${result.stderr || result.stdout}`);
  }
  return result.stdout || '';
}

function locateSourcesJar(version) {
  const root = path.join(os.homedir(), '.gradle', 'caches', 'modules-2', 'files-2.1', 'net.neoforged', 'neoforge', version);
  const targetName = `neoforge-${version}-sources.jar`;
  const matches = [];

  function visit(dir) {
    let entries;
    try {
      entries = fs.readdirSync(dir, { withFileTypes: true });
    } catch (_) {
      return;
    }
    for (const entry of entries) {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) visit(full);
      else if (entry.isFile() && entry.name === targetName) matches.push(full);
    }
  }

  visit(root);
  if (!matches.length) {
    throw new Error(`Could not find ${targetName} under ${root}`);
  }
  matches.sort();
  return matches[0];
}

function listJavaEntries(jarPath) {
  return run('jar', ['tf', jarPath])
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(line => line.endsWith('.java') && line.startsWith('net/neoforged/neoforge/'))
    .sort();
}

function normalizeIncludePrefix(value) {
  const raw = String(value || '').replace(/\\/g, '/').replace(/^\/+/, '');
  if (!raw) return '';
  return raw.endsWith('/') ? raw : `${raw}/`;
}

function jarIncludePrefix(value) {
  if (!value) return '';
  return value.startsWith('net/neoforged/neoforge/')
    ? value
    : `net/neoforged/neoforge/${value}`;
}

function cleanDir(dir) {
  fs.rmSync(dir, { recursive: true, force: true });
  fs.mkdirSync(dir, { recursive: true });
}

function extractEntries(jarPath, entries, outDir) {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'neoforge-api-extract-'));
  try {
    run('jar', ['xf', jarPath], { cwd: tmpDir });
    for (const entry of entries) {
      const source = path.join(tmpDir, ...entry.split('/'));
      if (!fs.existsSync(source)) continue;
      const relative = entry.replace(/^net\/neoforged\/neoforge\//, '');
      const dest = path.join(outDir, ...relative.split('/'));
      fs.mkdirSync(path.dirname(dest), { recursive: true });
      fs.copyFileSync(source, dest);
    }
  } finally {
    fs.rmSync(tmpDir, { recursive: true, force: true });
  }
}

function readExtractedJavaFiles(outDir) {
  const files = [];
  function visit(dir) {
    let entries;
    try {
      entries = fs.readdirSync(dir, { withFileTypes: true });
    } catch (_) {
      return;
    }
    for (const entry of entries) {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) visit(full);
      else if (entry.isFile() && entry.name.endsWith('.java')) files.push(full);
    }
  }
  visit(outDir);
  return files.sort();
}

function filterFileItemsByInclude(files, include) {
  if (!include) return files;
  return files.filter(item => item.relative === include.replace(/\/$/, '') || item.relative.startsWith(include));
}

function stripCommentStars(comment) {
  return comment
    .replace(/^\/\*\*?/, '')
    .replace(/\*\/$/, '')
    .split(/\r?\n/)
    .map(line => line.replace(/^\s*\*\s?/, '').trimEnd())
    .join('\n')
    .trim();
}

function cleanJavadoc(text) {
  return stripCommentStars(text)
    .replace(/\{@(?:link|linkplain|value)\s+([^}]+)\}/g, (_, value) => cleanInlineReference(value))
    .replace(/\{@(?:code|literal)\s+([^}]+)\}/g, '$1')
    .replace(/\{@[^}]+\}/g, '')
    .replace(/@implNote\b/gi, '')
    .replace(/<[^>]+>/g, '')
    .replace(/@(?:param|return|throws|see|since|author|deprecated)\b[\s\S]*$/i, '')
    .replace(/\s+/g, ' ')
    .trim();
}

function cleanInlineReference(value) {
  const text = String(value || '').trim();
  if (!text) return '';
  const parts = text.split(/\s+/);
  if (parts.length > 1) return parts.slice(1).join(' ');
  return parts[0]
    .replace(/^#/, '')
    .replace(/^[\w.]+\./, '')
    .replace(/\([^)]*\)/g, '');
}

function firstSentence(text) {
  const cleaned = cleanJavadoc(text);
  if (!cleaned) return '';
  const match = cleaned.match(/^(.+?(?:\.|!|\?))(?:\s|$)/);
  return terseSummary(match ? match[1] : cleaned);
}

function findTopLevelType(source) {
  const typePattern = /((?:\/\*\*[\s\S]*?\*\/\s*)?(?:(?:@[A-Za-z_][\w.]*\s*(?:\([^)]*\))?\s*)*)?(?:(?:public|protected|private|abstract|final|sealed|non-sealed|static)\s+)*(class|interface|enum|record|@interface)\s+([A-Za-z_$][\w$]*))/g;
  let match;
  while ((match = typePattern.exec(source))) {
    const before = source.slice(0, match.index);
    if (braceDepth(before) === 0) {
      const javadocMatch = match[1].match(/\/\*\*[\s\S]*?\*\//);
      return {
        kind: match[2],
        name: match[3],
        javadoc: javadocMatch ? javadocMatch[0] : '',
      };
    }
  }
  return null;
}

function braceDepth(text) {
  let depth = 0;
  const stripped = text
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .replace(/\/\/.*$/gm, '')
    .replace(/"(?:[^"\\]|\\.)*"/g, '""')
    .replace(/'(?:[^'\\]|\\.)*'/g, "''");
  for (const char of stripped) {
    if (char === '{') depth += 1;
    else if (char === '}') depth = Math.max(0, depth - 1);
  }
  return depth;
}

function countPublicMembers(source) {
  const withoutComments = source.replace(/\/\*[\s\S]*?\*\//g, '').replace(/\/\/.*$/gm, '');
  return {
    methods: (withoutComments.match(/^\s*public\s+(?:static\s+)?(?:<[^>]+>\s+)?[\w<>\[\], ?]+\s+[A-Za-z_$][\w$]*\s*\(/gm) || []).length,
    fields: (withoutComments.match(/^\s*public\s+(?:static\s+)?(?:final\s+)?[\w<>\[\], ?]+\s+[A-Z_a-z$][\w$]*(?:\s*=|;)/gm) || []).length,
    nestedTypes: (withoutComments.match(/^\s*public\s+(?:static\s+)?(?:class|interface|enum|record)\s+[A-Za-z_$][\w$]*/gm) || []).length,
  };
}

function summarizeFile(filePath, relativePath) {
  const source = fs.readFileSync(filePath, 'utf8');
  const type = findTopLevelType(source);
  const members = countPublicMembers(source);
  let summary = '';

  if (type) {
    summary = roleSummaryOverride(type.name);
  }
  if (!summary && type && type.javadoc) {
    summary = firstSentence(type.javadoc);
  }
  if (!summary && relativePath.endsWith('package-info.java')) {
    const packageDoc = source.match(/\/\*\*[\s\S]*?\*\//);
    if (packageDoc) summary = firstSentence(packageDoc[0]);
  }
  if (!summary && type) {
    summary = fallbackRoleSummary(type, members);
  }
  if (!summary) {
    summary = '';
  }

  return {
    relativePath,
    typeName: type ? type.name : path.basename(relativePath, '.java'),
    kind: type ? type.kind : 'source',
    summary,
  };
}

function terseSummary(text) {
  let value = String(text || '').trim();
  value = value.replace(/\.$/, '');
  value = value.replace(/\bThis is the\b/i, 'The');
  value = value.replace(/\bThis class is\b/i, 'Class for');
  value = value.replace(/\bThis interface is\b/i, 'Interface for');
  value = value.replace(/\bThis event is\b/i, 'Event for');
  value = value.replace(/\bFired to gather information for\b/i, 'Event for');
  value = value.replace(/\bA BlockCapability gives\b/i, 'Provides');
  value = value.replace(/\bRepresents the basic unit at the heart of\b/i, 'Core unit for');
  value = value.replace(/\bDefault PermissionTypes, if you need additional ones, please PR it\b/i, 'Built-in permission value types');
  value = value.replace(/\bThe Heart of the PermissionAPI, it manages PermissionNodes as well as it handles all permission queries\b/i, 'Permission node lookup and query interface');
  value = value.replace(/\bType of a Permission, use the existing Types in PermissionTypes.*$/i, 'Permission value type descriptor');
  value = value.replace(/\s+/g, ' ').trim();
  const words = value.split(/\s+/);
  if (words.length > 18) value = `${words.slice(0, 18).join(' ')}...`;
  return value;
}

function roleSummaryOverride(name) {
  const overrides = {
    PermissionAPI: 'Entry point for permission APIs',
    PermissionGatherEvent: 'Event for registering permission handlers and nodes',
    IPermissionHandler: 'Permission node lookup and query interface',
    IPermissionHandlerFactory: 'Factory for permission handlers',
    PermissionDynamicContext: 'Typed dynamic context value for permission checks',
    PermissionDynamicContextKey: 'Key for typed permission dynamic contexts',
    PermissionNode: 'Defines a permission key, type, resolver, and contexts',
    PermissionType: 'Permission value type descriptor',
    PermissionTypes: 'Built-in permission value types',
  };
  return overrides[name] || '';
}

function splitWords(name) {
  const cleaned = String(name || '').replace(/^I(?=[A-Z][a-z])/, '');
  return cleaned
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2')
    .replace(/[_-]+/g, ' ')
    .toLowerCase();
}

function fallbackRoleSummary(type, members) {
  const words = splitWords(type.name);
  if (/event\b/.test(words)) return `${capitalize(type.kind)} for ${words.replace(/\bevent\b/, '').trim()} events`;
  if (/api\b/.test(words)) return `Entry point for ${words.replace(/\bapi\b/, '').trim()} APIs`;
  if (/handler\b/.test(words)) return `${capitalize(type.kind)} for ${words.replace(/\bhandler\b/, '').trim()} handling`;
  if (/factory\b/.test(words)) return `${capitalize(type.kind)} for creating ${words.replace(/\bfactory\b/, '').trim()} instances`;
  if (/registry\b/.test(words)) return `${capitalize(type.kind)} for ${words.replace(/\bregistry\b/, '').trim()} registration`;
  if (/type\b/.test(words)) return `${capitalize(type.kind)} describing ${words.replace(/\btype\b/, '').trim()} types`;
  if (/exception\b/.test(words)) return `${capitalize(type.kind)} for ${words.replace(/\bexception\b/, '').trim()} failures`;
  if (members.methods || members.fields || members.nestedTypes) return `${capitalize(type.kind)} for ${words}`;
  return `${capitalize(type.kind)} ${type.name}`;
}

function capitalize(value) {
  return String(value || '').charAt(0).toUpperCase() + String(value || '').slice(1);
}

function groupByDirectory(entries) {
  const groups = new Map();
  for (const entry of entries) {
    const dir = path.dirname(entry.relativePath).replace(/\\/g, '/');
    if (!groups.has(dir)) groups.set(dir, []);
    groups.get(dir).push(entry);
  }
  return [...groups.entries()].sort(([a], [b]) => a.localeCompare(b));
}

function buildIndex(entries, options) {
  const apiName = options.apiName || 'NeoForge';
  const sourceDesc = options.sourceDesc || options.jar || options.outDir || '';
  const outDirRelative = options.outDir
    ? path.relative(process.cwd(), options.outDir).replace(/\\/g, '/')
    : 'docs/api';
  const versionLine = options.version
    ? `Generated from ${apiName} ${options.version} source files.`
    : `Generated from ${apiName} source files.`;
  const lines = [
    `# ${apiName} API Index`,
    '',
    `Source: \`${sourceDesc}\``,
    versionLine,
    '',
    `Use this index to identify the specific source reference file to load from \`${outDirRelative}\`.`,
    '',
    `Indexed Java files: ${entries.length}`,
    '',
  ];

  for (const [dir, group] of groupByDirectory(entries)) {
    lines.push(`## ${dir === '.' ? '(root)' : dir}`);
    lines.push('');
    for (const entry of group.sort((a, b) => a.relativePath.localeCompare(b.relativePath))) {
      if (!entry.summary && path.basename(entry.relativePath) === 'package-info.java') continue;
      if (!entry.summary) continue;
      lines.push(`- \`${entry.relativePath}\` — ${entry.summary}`);
    }
    lines.push('');
  }
  return `${lines.join('\n').trimEnd()}\n`;
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  const outDir = path.resolve(args.outDir);
  const indexPath = path.resolve(args.index);
  const apiName = args.apiName || 'NeoForge';

  if (args.noExtract) {
    const allFiles = readExtractedJavaFiles(outDir).map(file => ({
      file,
      relative: path.relative(outDir, file).replace(/\\/g, '/'),
    }));
    const filtered = filterFileItemsByInclude(allFiles, args.include);
    const selected = args.limit > 0 ? filtered.slice(0, args.limit) : filtered;
    const summaries = selected.map(item => summarizeFile(item.file, item.relative));
    const sourceDesc = args.sourceDesc || outDir;

    if (args.dryRun) {
      console.log(`[dry-run] Source directory: ${outDir}`);
      if (args.include) console.log(`[dry-run] Include prefix: ${args.include}`);
      console.log(`[dry-run] Java files found: ${allFiles.length}`);
      console.log(`[dry-run] Java files processed: ${summaries.length}`);
      console.log('[dry-run] Sample index entries:');
      const printable = summaries.filter(e => e.summary);
      for (const entry of printable.slice(0, Math.min(20, printable.length))) {
        console.log(`- ${entry.relativePath} — ${entry.summary}`);
      }
      return;
    }

    fs.mkdirSync(path.dirname(indexPath), { recursive: true });
    fs.writeFileSync(indexPath, buildIndex(summaries, { apiName, sourceDesc, version: args.version, outDir }), 'utf8');
    console.log(`Wrote ${summaries.length} index entries to ${indexPath}`);
    return;
  }

  const jar = path.resolve(args.jar || locateSourcesJar(args.version));
  const jarEntries = listJavaEntries(jar);
  const includeForJar = jarIncludePrefix(args.include);
  const filteredEntries = includeForJar
    ? jarEntries.filter(entry => entry.startsWith(includeForJar))
    : jarEntries;
  const selectedEntries = args.limit > 0 ? filteredEntries.slice(0, args.limit) : filteredEntries;
  const sourceDesc = args.sourceDesc || jar;

  if (args.dryRun) {
    const tmpOut = fs.mkdtempSync(path.join(os.tmpdir(), 'api-dry-run-'));
    try {
      extractEntries(jar, selectedEntries, tmpOut);
      const files = readExtractedJavaFiles(tmpOut).map(file => ({
        file,
        relative: path.relative(tmpOut, file).replace(/\\/g, '/'),
      }));
      const summaries = files.map(item => summarizeFile(item.file, item.relative));
      console.log(`[dry-run] Source jar: ${jar}`);
      console.log(`[dry-run] Java files in jar: ${jarEntries.length}`);
      if (args.include) console.log(`[dry-run] Include prefix: ${includeForJar}`);
      if (args.include) console.log(`[dry-run] Java files matching prefix: ${filteredEntries.length}`);
      console.log(`[dry-run] Java files processed: ${summaries.length}`);
      console.log('[dry-run] Sample index entries:');
      const printable = summaries.filter(e => e.summary);
      for (const entry of printable.slice(0, Math.min(20, printable.length))) {
        console.log(`- ${entry.relativePath} — ${entry.summary}`);
      }
    } finally {
      fs.rmSync(tmpOut, { recursive: true, force: true });
    }
    return;
  }

  cleanDir(outDir);
  extractEntries(jar, selectedEntries, outDir);
  const files = readExtractedJavaFiles(outDir).map(file => ({
    file,
    relative: path.relative(outDir, file).replace(/\\/g, '/'),
  }));
  const summaries = files.map(item => summarizeFile(item.file, item.relative));
  fs.mkdirSync(path.dirname(indexPath), { recursive: true });
  fs.writeFileSync(indexPath, buildIndex(summaries, { apiName, sourceDesc, version: args.version, outDir }), 'utf8');
  console.log(`Extracted ${selectedEntries.length} Java files to ${outDir}`);
  console.log(`Wrote ${summaries.length} index entries to ${indexPath}`);
}

main();
