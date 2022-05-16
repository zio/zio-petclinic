import {resolve} from 'path'

// https://vitejs.dev/config/
export default ({mode}) => {
    return {
        publicDir: './src/main/static/public',
        resolve: {
            alias: {
                'stylesheets': resolve(__dirname, './frontend/src/main/static/stylesheets'),
            }
        }
    }
}
