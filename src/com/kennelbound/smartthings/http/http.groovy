include 'asynchttp_v1'
declare("HTTP", "com_kennelbound_smartthings_http")

def post(uri, path, body, contentType, headers, callbackMethodName, passthru = null) {
    String stringBody = body?.collect { k, v -> "$k=$v" }?.join("&")?.toString() ?: ""

    def params = [
            uri               : uri,
            path              : path,
            body              : stringBody,
            headers           : headers,
            requestContentType: "application/x-www-form-urlencoded",
            contentType       : contentType
    ]

    def data = [
            path       : path,
            passthru   : passthru,
            contentType: contentType
    ]

    try {
        asynchttp_v1.post(callbackMethodName, params, data)
        state.referer = "${params['uri']}$path"
    } catch (e) {
        log.error "Something unexpected went wrong in HTTP.post: ${e}"
    }
}

def get(uri, path, query, contentType, headers, callbackMethodName, passthru = null) {
    def params = [
            uri               : uri,
            path              : path,
            query             : query,
            headers           : headers,
            requestContentType: contentType
    ]

    def data = [
            path       : path,
            passthru   : passthru,
            contentType: contentType
    ]

    try {
//        log.debug("Attempting to get: $data")
        asynchttp_v1.get(callbackMethodName, params, data)
        state.referer = "${params['uri']}$path"
    } catch (e) {
        log.error "Something unexpected went wrong in HTTP.get: ${e}: $e.stackTrace"
    }
}

def setupCookie(response) {
    try {
        state.cookie = response?.headers?.'Set-Cookie'?.split(';')?.getAt(0) ?: state.cookie ?: state.cookie
    } catch (Exception e) {
//        log.warn "Couldn't fetch cookie and token from response, $e"
    }
}
