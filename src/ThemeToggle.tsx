import { useTheme } from './theme'

export default function ThemeToggle() {
  const { theme, chooseTheme } = useTheme()
  return <div className="theme-toggle" role="group" aria-label="Tema da interface">
    <button type="button" aria-label="Tema claro" aria-pressed={theme === 'light'} onClick={() => chooseTheme('light')}>
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" aria-hidden="true"><circle cx="12" cy="12" r="4"/><path d="M12 2v2m0 16v2M2 12h2m16 0h2M5 5l1.5 1.5m11 11L19 19M5 19l1.5-1.5m11-11L19 5"/></svg>
      Claro
    </button>
    <button type="button" aria-label="Tema escuro" aria-pressed={theme === 'dark'} onClick={() => chooseTheme('dark')}>
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true"><path d="M20.5 13A8.5 8.5 0 0 1 11 3.5 8.5 8.5 0 1 0 20.5 13Z"/></svg>
      Escuro
    </button>
  </div>
}
