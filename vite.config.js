import {resolve} from 'path'

// https://vitejs.dev/config/
export default {
    resolve: {
        alias: {
            '/stylesheets': resolve(__dirname, '/frontend/src/main/static/stylesheets'),
        }
    }
}
