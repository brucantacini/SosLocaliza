import { mkdirSync, writeFileSync } from 'node:fs'
const source = 'https://fonts.googleapis.com/css2?family=Montserrat:wght@100..900&display=swap'
const response = await fetch(source, { headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36' } })
if (!response.ok) throw new Error(`Font CSS: ${response.status}`)
const css = await response.text()
const latin = css.split('/* latin */')[1]
const url = latin?.match(/url\(([^)]+\.woff2)\)/)?.[1]
if (!url) throw new Error('Latin WOFF2 not found')
const font = await fetch(url)
if (!font.ok) throw new Error(`Font download: ${font.status}`)
mkdirSync('public/fonts', { recursive: true })
writeFileSync('public/fonts/montserrat-latin.woff2', Buffer.from(await font.arrayBuffer()))
const license = await fetch('https://raw.githubusercontent.com/google/fonts/main/ofl/montserrat/OFL.txt')
if (!license.ok) throw new Error(`License: ${license.status}`)
writeFileSync('public/fonts/OFL.txt', await license.text())
writeFileSync('public/fonts/SOURCE.txt', `Montserrat variable, normal, Latin (includes Portuguese).\nSource CSS: ${source}\nFont source: ${url}\nLicense: SIL Open Font License 1.1, see OFL.txt.\nSelf-hosted; the app makes no requests to a font service.\n`)
console.log('Montserrat WOFF2 and license saved locally.')
