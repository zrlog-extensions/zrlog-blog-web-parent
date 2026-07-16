(() => {
    'use strict'

    const root = document.documentElement
    const systemTheme = window.matchMedia('(prefers-color-scheme: dark)')
    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)')
    const allowedThemes = new Set(['auto', 'light', 'dark'])
    let preference = 'auto'
    let transitionTimer

    try {
        const storedTheme = window.localStorage.getItem('theme')
        if (storedTheme && allowedThemes.has(storedTheme)) {
            preference = storedTheme
        }
    } catch (_) {
        // Storage can be disabled without making theme rendering fail.
    }

    const resolveTheme = value => value === 'auto' ? (systemTheme.matches ? 'dark' : 'light') : value

    const syncHighlightTheme = theme => {
        const lightTheme = document.getElementById('hljs-light-theme')
        const darkTheme = document.getElementById('hljs-dark-theme')
        if (lightTheme && darkTheme) {
            lightTheme.disabled = theme === 'dark'
            darkTheme.disabled = theme !== 'dark'
        }
    }

    const applyTheme = (value, animate = false) => {
        const theme = resolveTheme(value)
        if (animate && !reducedMotion.matches) {
            root.classList.add('theme-changing')
            window.clearTimeout(transitionTimer)
            transitionTimer = window.setTimeout(() => root.classList.remove('theme-changing'), 220)
        }
        root.dataset.theme = theme
        root.dataset.themePreference = value
        root.style.colorScheme = theme
        syncHighlightTheme(theme)
    }

    applyTheme(preference)

    systemTheme.addEventListener('change', () => {
        if (preference === 'auto') {
            applyTheme(preference, true)
        }
    })

    window.addEventListener('DOMContentLoaded', () => {
        const themeMenu = document.querySelector('.theme-menu')
        const themeTrigger = document.querySelector('.theme-menu-trigger')
        const themeOptions = [...document.querySelectorAll('[data-theme-value]')]
        const menuButton = document.querySelector('.menu-toggle')
        const navigation = document.getElementById('primary-navigation')

        const syncThemeControls = () => {
            let activeLabel = ''
            themeOptions.forEach(option => {
                const active = option.dataset.themeValue === preference
                option.setAttribute('aria-pressed', String(active))
                if (active) {
                    activeLabel = option.querySelector('.theme-option-label')?.textContent || ''
                }
            })
            if (themeTrigger) {
                themeTrigger.setAttribute('aria-label', `${themeTrigger.dataset.label}: ${activeLabel}`)
            }
        }

        if (themeMenu && themeOptions.length) {
            syncThemeControls()
            themeOptions.forEach(option => option.addEventListener('click', () => {
                const nextTheme = option.dataset.themeValue
                if (!allowedThemes.has(nextTheme)) {
                    return
                }
                preference = nextTheme
                try {
                    window.localStorage.setItem('theme', preference)
                } catch (_) {
                    // Keep the in-memory preference when storage is unavailable.
                }
                applyTheme(preference, true)
                syncThemeControls()
                themeMenu.open = false
                themeTrigger?.focus()
            }))
        }

        const closeMenu = () => {
            if (menuButton && navigation) {
                menuButton.setAttribute('aria-expanded', 'false')
                navigation.dataset.open = 'false'
            }
        }

        if (menuButton && navigation) {
            menuButton.addEventListener('click', () => {
                const open = navigation.dataset.open !== 'true'
                if (themeMenu) {
                    themeMenu.open = false
                }
                navigation.dataset.open = String(open)
                menuButton.setAttribute('aria-expanded', String(open))
            })
            navigation.addEventListener('click', event => {
                if (event.target.closest('a')) {
                    closeMenu()
                }
            })
            window.addEventListener('resize', () => {
                if (window.innerWidth > 860) {
                    closeMenu()
                }
            })
            document.addEventListener('pointerdown', event => {
                if (navigation.dataset.open === 'true' &&
                    !navigation.contains(event.target) && !menuButton.contains(event.target)) {
                    closeMenu()
                }
                if (themeMenu?.open && !themeMenu.contains(event.target)) {
                    themeMenu.open = false
                }
            })
            themeMenu?.addEventListener('toggle', () => {
                if (themeMenu.open) {
                    closeMenu()
                }
            })
            document.addEventListener('keydown', event => {
                if (event.key === 'Escape') {
                    if (navigation.dataset.open === 'true') {
                        closeMenu()
                        menuButton.focus()
                    }
                    if (themeMenu?.open) {
                        themeMenu.open = false
                        themeTrigger?.focus()
                    }
                }
            })
        }
    })
})()
