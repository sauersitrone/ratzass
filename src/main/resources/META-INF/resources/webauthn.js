/**
 * TERRY ACHTUNG: Das ist eine Copy aus ursprüngliche q/webauthn/webauthn.js von quarkus-security-webauthn. 
 */


/*
 * Base64URL-ArrayBuffer
 * https://github.com/herrjemand/Base64URL-ArrayBuffer
 *
 * Copyright (c) 2017 Yuriy Ackermann <ackermann.yuriy@gmail.com>
 * Copyright (c) 2012 Niklas von Hertzen
 * Licensed under the MIT license.
 */
const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_';
// Use a lookup table to find the index.
const lookup = new Uint8Array(256);

for (let i = 0; i < chars.length; i++) {
    lookup[chars.charCodeAt(i)] = i;
}

const bufferToBase64 = function (arraybuffer) {
    const bytes = new Uint8Array(arraybuffer);

    let i;
    let len = bytes.length;
    let base64url = '';

    for (i = 0; i < len; i += 3) {
        base64url += chars[bytes[i] >> 2];
        base64url += chars[((bytes[i] & 3) << 4) | (bytes[i + 1] >> 4)];
        base64url += chars[((bytes[i + 1] & 15) << 2) | (bytes[i + 2] >> 6)];
        base64url += chars[bytes[i + 2] & 63];
    }

    if ((len % 3) === 2) {
        base64url = base64url.substring(0, base64url.length - 1);
    } else if (len % 3 === 1) {
        base64url = base64url.substring(0, base64url.length - 2);
    }

    return base64url;
}

const base64ToBuffer = function (base64string) {
    if (base64string) {

        let bufferLength = base64string.length * 0.75;

        let len = base64string.length;
        let i;
        let p = 0;

        let encoded1;
        let encoded2;
        let encoded3;
        let encoded4;

        let bytes = new Uint8Array(bufferLength);

        for (i = 0; i < len; i += 4) {
            encoded1 = lookup[base64string.charCodeAt(i)];
            encoded2 = lookup[base64string.charCodeAt(i + 1)];
            encoded3 = lookup[base64string.charCodeAt(i + 2)];
            encoded4 = lookup[base64string.charCodeAt(i + 3)];

            bytes[p++] = (encoded1 << 2) | (encoded2 >> 4);
            bytes[p++] = ((encoded2 & 15) << 4) | (encoded3 >> 2);
            bytes[p++] = ((encoded3 & 3) << 6) | (encoded4 & 63);
        }

        return bytes.buffer;
    }
}

const callbackPath = '/q/webauthn/callback';
const registerPath = '/q/webauthn/register';
const loginPath = '/q/webauthn/login';

function registerOnly(userName, firstName, lastName) {
    const user = { name: userName, displayName: firstName + " " + lastName };
    return fetch(registerPath, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user || {})
    })
        .then(res => {
            if (res.status === 200) {
                return res;
            }
            throw new Error(res.statusText);
        })
        .then(res => res.json())
        .then(res => {
            res.challenge = base64ToBuffer(res.challenge);
            res.user.id = base64ToBuffer(res.user.id);
            if (res.excludeCredentials) {
                for (const element of res.excludeCredentials) {
                    element.id = base64ToBuffer(element.id);
                }
            }
            return res;
        })
        .then(res => navigator.credentials.create({ publicKey: res }))
        .then(credential => {
            return {
                id: credential.id,
                rawId: bufferToBase64(credential.rawId),
                response: {
                    attestationObject: bufferToBase64(credential.response.attestationObject),
                    clientDataJSON: bufferToBase64(credential.response.clientDataJSON)
                },
                type: credential.type
            };
        });
};

/**
 * registration method from vaading
 * 
 * @param {*} userName - the username (user)
 * @param {*} firstName - first name
 * @param {*} lastName - last name
 */
function registerFromVaadin(userName, firstName, lastName) {
    let element = document.getElementById("de.simone.uiviews.MainLayout");
    register(userName, firstName, lastName)
        .then(body => {
            const rStatus = "ok.";
            element.$server.registrationStaus(rStatus);
        })
        .catch(err => {
            const rStatus = "" + err;
            element.$server.registrationStaus(rStatus);
        });

}


function register(userName, firstName, lastName) {
    return registerOnly(userName, firstName, lastName)
        .then(body => {
            return fetch(callbackPath, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            })
        })
        .then(response => {
            if (response.status >= 200 && response.status < 300) {
                return response;
            }
            throw new Error(response.statusText);
        });
};

function logInFromVaadin(userName) {
    let element = document.getElementById("de.simone.goodmfa.externalloging.SignInForm");
    login(userName)
        .then(body => {
            const rStatus = "ok.";
            element.$server.logInStaus(rStatus);
        })
        .catch(err => {
            const rStatus = "" + err;
            element.$server.logInStaus(rStatus);
        });

}


function login(userName) {
    return loginOnly(userName)
        .then(body => {
            return fetch(callbackPath, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body),
            })
        })
        .then(res => {
            if (res.status >= 200 && res.status < 300) {
                return res;
            }
            throw new Error(res.statusText);
        });
};

function loginOnly(userName) {
    const user = { name: userName }
    return fetch(loginPath, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(res => {
            if (res.status === 200) {
                return res;
            }
            throw new Error(res.statusText);
        })
        .then(res => res.json())
        .then(res => {
            res.challenge = base64ToBuffer(res.challenge);
            if (res.allowCredentials) {
                for (const element of res.allowCredentials) {
                    element.id = base64ToBuffer(element.id);
                }
            }
            return res;
        })
        .then(res => navigator.credentials.get({ publicKey: res }))
        .then(credential => {
            return {
                id: credential.id,
                rawId: bufferToBase64(credential.rawId),
                response: {
                    clientDataJSON: bufferToBase64(credential.response.clientDataJSON),
                    authenticatorData: bufferToBase64(credential.response.authenticatorData),
                    signature: bufferToBase64(credential.response.signature),
                    userHandle: bufferToBase64(credential.response.userHandle),
                },
                type: credential.type
            };
        })
};
