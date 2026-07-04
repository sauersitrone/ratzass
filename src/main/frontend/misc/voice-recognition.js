import { LitElement, html, css } from 'lit';

class VoiceRecognition extends LitElement {

  static get styles() {
    return css`
      :host {
        display: block;
      }
      .recording {
        background-color: grey;
      }
      button {
        cursor: pointer;
        padding: 10px 20px;
        font-size: 14px;
        border: none;
        border-radius: 5px;
        color: white;
        background-color: #007BFF;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        outline: none;
        transition: background-color 0.3s, box-shadow 0.3s;
        margin-right: 5px;
      }
      button:hover {
        background-color: #0069D9;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      }
      button:active {
        background-color: #0062CC;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      }
      button:disabled {
        background-color: #6C757D;
        cursor: not-allowed;
        box-shadow: none;
      }
      .clear-speech {
        background-color: #FFC107;
      }
      .clear-speech:hover {
        background-color: #FFB300;
      }
      .clear-speech:active {
        background-color: #FFA000;
      }
    `;
  }


  static get properties() {
    return {
      continuous: { type: Boolean },
      speech: { type: String },
      isRecording: { type: Boolean },
      useInternalButtons: { type: Boolean },
    };
  }

  constructor() {
    super();
    this.initializeRecognition();
    this.speech = `Press the 'Start' button to start recording your voice.`;
    this.isRecording = false;
    this.lastResultIndex = 0;
    this.useInternalButtons = true;
  }

  connectedCallback() {
    super.connectedCallback();
    this.initializeAttributes();
    this.initializeEventListeners();
  }

  initializeRecognition() {
    const SpeechRecognition =
      window.SpeechRecognition ||
      window.webkitSpeechRecognition ||
      window.mozSpeechRecognition ||
      window.msSpeechRecognition ||
      window.oSpeechRecognition;

    this.recognition =
      SpeechRecognition !== undefined
        ? new SpeechRecognition()
        : console.error('Your browser does not support the Web Speech API');

    this.useInternalButtons = this.hasAttribute('use-internal-buttons') || true;

  }

  initializeAttributes() {
    this.continuous = this.hasAttribute('continuous');
    this.speech = this.getAttribute('speech') || '';

    if (this.recognition) {
      this.recognition.continuous = this.continuous;
      this.recognition.interimResults = false;
    }
  }

  initializeEventListeners() {
    if (!this.recognition) {
      console.log('recognition not initialized');
      return;
    }

    ['start', 'end', 'error', 'speechResult'].forEach((eventName) =>
      this.recognition.addEventListener(eventName, (e) =>
        this.dispatchEvent(new CustomEvent(eventName, { detail: e }))
      )
    );

    this.recognition.addEventListener('result', (e) => {
      const newResults = [...Array(e.results.length).keys()]
        .slice(this.lastResultIndex)
        .map((i) => e.results[i][0].transcript);
      this.speech = this.speech + newResults.join(' ');
      this.lastResultIndex = e.results.length;

      this.dispatchEvent(new CustomEvent('speechResult', { detail: { speech: this.speech } }));
    });

  }

  start() {
    if (!this.isRecording) {
      this.recognition.start();
      this.isRecording = true;
      this.lastResultIndex = 0; // Add this line
    } else {
      this.recognition.stop();
      this.isRecording = false;
    }
  }


  stop() {
    this.recognition.stop();
  }

  abort() {
    this.recognition.abort();
  }

  clearSpeech() {
    this.speech = '';
  }

  render() {
    return html`
      <slot></slot>
      ${this.useInternalButtons
        ? html`
            <button
              class="${this.isRecording ? 'recording' : ''}"
              @click="${this.start}"
            >
              ${this.isRecording ? 'Stop' : 'Start'}
            </button>
            <button class="clear-speech" @click="${this.clearSpeech}">Clear Speech</button>
          `
        : ''}
    `;
  }

}

customElements.define('voice-recognition', VoiceRecognition);