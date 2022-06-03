const colors = require('tailwindcss/colors')
const typography = require('@tailwindcss/typography')
const forms = require('@tailwindcss/forms')
const path = require('path')

module.exports = {
    content: [
        path.resolve(__dirname, './*.html'),
        path.resolve(__dirname, `./**/*.js`),
        path.resolve(__dirname, `./**/*.html`),
    ],
    safelist: ["mt-8"],
    theme: {
        extend: {
            fontFamily: {
                serif: ['Inter', 'ui-serif', 'Georgia', 'Cambria', '"Times New Roman"', 'Times', 'serif'],
            },
            colors: {
                gray: colors.stone,
                orange: colors.orange,
                cyan: colors.cyan
            },
        },
    },
    variants: {
        extend: {
            transitionDuration: ['hover']
        }
    },
    corePlugins: {},
    plugins: [typography, forms],
}