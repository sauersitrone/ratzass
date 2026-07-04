const synth = window.speechSynthesis;
const pitch = 1;
const rate = 1;
const currentVoice = 0;
let voices = [];

function populateVoiceList() {
    voices = synth.getVoices().sort(function (a, b) {
        const aname = a.name.toUpperCase();
        const bname = b.name.toUpperCase();

        if (aname < bname) {
            return -1;
        } else if (aname == bname) {
            return 0;
        } else {
            return +1;
        }
    });
}

populateVoiceList();

if (speechSynthesis.onvoiceschanged !== undefined) {
    speechSynthesis.onvoiceschanged = populateVoiceList;
}

window.getVoices = function () {
    return JSON.stringify(voices);
}

window.speak = function (voice, text) {
    if (synth.speaking) {
        console.error("speechSynthesis.speaking");
        return;
    }

    if (text !== "") {
        const utterThis = new SpeechSynthesisUtterance(text);

        utterThis.onend = function (event) {
            console.log("SpeechSynthesisUtterance.onend");
        };

        utterThis.onerror = function (event) {
            console.error("SpeechSynthesisUtterance.onerror");
        };

        for (let i = 0; i < voices.length; i++) {
            if (voices[i].name === voice) {
                utterThis.voice = voices[i];
                break;
            }
        }
        console.log(voice);
        console.log(text);
        utterThis.pitch = pitch;
        utterThis.rate = rate;
        synth.speak(utterThis);
    }
}
