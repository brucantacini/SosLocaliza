import { useEffect, useRef, useState } from 'react'

export type Theme = 'light' | 'dark'
export const THEME_KEY = 'sos-localiza-theme'
const SYSTEM_THEME = '(prefers-color-scheme: dark)'
const browserColors: Record<Theme, string> = { light: '#F1F0ED', dark: '#102A43' }

function parseTheme(value: string | null | undefined): Theme | null {
  return value === 'light' || value === 'dark' ? value : null
}

function savedTheme(): Theme | null {
  try { return parseTheme(localStorage.getItem(THEME_KEY)) } catch { return null }
}

function deviceTheme(): Theme {
  return window.matchMedia(SYSTEM_THEME).matches ? 'dark' : 'light'
}

function applyTheme(theme: Theme) {
  document.documentElement.dataset.theme = theme
  document.querySelector('meta[name="theme-color"]')?.setAttribute('content', browserColors[theme])
}

export function useTheme() {
  const preference = useRef<Theme | null>(savedTheme())
  const [theme, setTheme] = useState<Theme>(() =>
    parseTheme(document.documentElement.dataset.theme) || preference.current || deviceTheme(),
  )

  useEffect(() => {
    const media = window.matchMedia(SYSTEM_THEME)
    const update = (next: Theme) => { applyTheme(next); setTheme(next) }
    const onDeviceChange = () => {
      if (preference.current === null) update(media.matches ? 'dark' : 'light')
    }
    const onStorage = (event: StorageEvent) => {
      if (event.key !== THEME_KEY && event.key !== null) return
      preference.current = parseTheme(event.newValue)
      update(preference.current || (media.matches ? 'dark' : 'light'))
    }
    applyTheme(theme)
    media.addEventListener('change', onDeviceChange)
    window.addEventListener('storage', onStorage)
    return () => {
      media.removeEventListener('change', onDeviceChange)
      window.removeEventListener('storage', onStorage)
    }
    // Listeners read preference.current so manual changes do not recreate them.
  }, [])

  function chooseTheme(next: Theme) {
    preference.current = next
    applyTheme(next)
    setTheme(next)
    try { localStorage.setItem(THEME_KEY, next) } catch { /* Keep the session choice. */ }
  }

  return { theme, chooseTheme }
}
