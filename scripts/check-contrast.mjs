import {Browser,sleep} from './lib/cdp.mjs'
import assert from 'node:assert/strict'
import {mkdirSync,writeFileSync} from 'node:fs'

const luminance=value=>{
  let hex=value.replace('#','').trim()
  if(hex.length===3)hex=[...hex].map(x=>x+x).join('')
  const rgb=hex.match(/../g).slice(0,3).map(x=>parseInt(x,16)/255).map(x=>x<=.04045?x/12.92:((x+.055)/1.055)**2.4)
  return rgb[0]*.2126+rgb[1]*.7152+rgb[2]*.0722
}
const ratio=(a,b)=>{const x=luminance(a),y=luminance(b);return(Math.max(x,y)+.05)/(Math.min(x,y)+.05)}
const browser=await Browser.launch()
const results=[]
try{
  const page=await browser.newPage()
  await page.goto((process.env.SOS_URL || 'http://127.0.0.1:5173/'),{settleMs:300})
  for(const theme of ['light','dark']){
    await page.eval(theme=>document.querySelector(`[aria-label="Tema ${theme==='dark'?'escuro':'claro'}"]`).click(),theme)
    await sleep(100)
    const tokens=await page.eval(()=>{
      const style=getComputedStyle(document.documentElement)
      return Object.fromEntries(['background','surface','surface-soft','ink','muted','placeholder','secondary-hover','selected-surface','on-accent','on-alert','accent','alert-action','danger-text','danger-surface','warning-text','warning-surface','focus','control-border','location','risk-high','risk-moderate','risk-low'].map(name=>[name,style.getPropertyValue('--'+name).trim()]))
    })
    const pairs=[['ink','background'],['ink','surface'],['muted','background'],['muted','surface'],['muted','surface-soft'],['placeholder','surface'],['ink','secondary-hover'],['ink','selected-surface'],['on-accent','accent'],['on-alert','alert-action'],['danger-text','danger-surface'],['warning-text','warning-surface']]
    for(const [fg,bg] of pairs){
      const contrast=ratio(tokens[fg],tokens[bg]);assert.ok(contrast>=4.5,`${theme} ${fg}/${bg}: ${contrast}`)
      results.push({theme,pair:`${fg}/${bg}`,ratio:Number(contrast.toFixed(2)),minimum:4.5})
    }
    for(const [fg,bg] of [['focus','background'],['focus','surface'],['control-border','surface']]){
      const contrast=ratio(tokens[fg],tokens[bg]);assert.ok(contrast>=3,`${theme} ${fg}/${bg}: ${contrast}`)
      results.push({theme,pair:`${fg}/${bg}`,ratio:Number(contrast.toFixed(2)),minimum:3})
    }
    for(const [token,color] of Object.entries({location:'#2f80ed','risk-high':'#d64550','risk-moderate':'#e9a820','risk-low':'#258a62','alert-action':'#cf3c48'}))assert.equal(tokens[token].toLowerCase(),color,'Semantic color must be invariant')
  }
  await page.close()
  mkdirSync('.impeccable/review/themes',{recursive:true})
  writeFileSync('.impeccable/review/themes/contrast.json',JSON.stringify(results,null,2))
  console.log(`PASS: ${results.length} text, placeholder, state, focus and border contrast pairs; semantic colors identical in both themes.`)
}finally{await browser.close()}
