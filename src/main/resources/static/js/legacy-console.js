const terminal = document.getElementById("terminal");
const command = document.getElementById("command");
const send = document.getElementById("send");
const clear = document.getElementById("clear");
const help = document.getElementById("help");
const reset = document.getElementById("reset");
const statusEl = document.getElementById("sessionStatus");
let sessionId = null;
let stream = null;
let processing = false;
const MAX_TERMINAL_LINES = 800;

function print(text) {
  terminal.textContent += text + "\n";
  trimTerminal();
  terminal.scrollTop = terminal.scrollHeight;
}

function trimTerminal() {
  const lines = terminal.textContent.split("\n");
  if (lines.length > MAX_TERMINAL_LINES) {
    terminal.textContent = lines.slice(lines.length - MAX_TERMINAL_LINES).join("\n");
  }
}

function setProcessing(value) {
  processing = value;
  send.disabled = value;
  command.disabled = value;
  statusEl.textContent = sessionId ? `Session ${sessionId}${value ? " - processing..." : ""}` : "Starting session...";
}

function printHelp() {
  print(`Available commands:
help  - show this help
clear - clear visible terminal output
cls   - clear visible terminal output
reset - restart the simulated Java shop session

Use menu numbers for the original console flow:
1, 2, 3, 4, 5 and prompts such as customer IDs or product IDs.`);
}

async function createSession() {
  setProcessing(true);
  const response = await fetch("/api/legacy-console/session", { method: "POST" });
  const result = await response.json();
  sessionId = result.sessionId;
  terminal.textContent = "";
  print(result.output);
  statusEl.textContent = `Session ${sessionId}`;
  openStream();
  setProcessing(false);
  command.focus();
}

function openStream() {
  if (stream) stream.close();
  stream = new EventSource(`/sse/legacy-console/${sessionId}`);
  stream.onmessage = event => {
    const result = JSON.parse(event.data);
    if (result.clearScreen) terminal.textContent = "";
    if (result.output) print(result.output);
  };
}

async function submitInput() {
  const input = command.value.trim();
  if (processing || !sessionId) return;
  if (!input) {
    command.focus();
    return;
  }
  if (input.toLowerCase() === "clear" || input.toLowerCase() === "cls") {
    terminal.textContent = "";
    command.value = "";
    command.focus();
    return;
  }
  if (input.toLowerCase() === "help") {
    print(`> ${input}`);
    printHelp();
    command.value = "";
    command.focus();
    return;
  }
  if (input.toLowerCase() === "reset") {
    command.value = "";
    await resetSession();
    return;
  }
  print(`> ${input}`);
  command.value = "";
  setProcessing(true);
  await fetch(`/api/legacy-console/session/${sessionId}/input`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ input })
  });
  setProcessing(false);
  command.focus();
}

send.addEventListener("click", submitInput);
command.addEventListener("keydown", event => {
  if (event.key === "Enter") submitInput();
});
async function resetSession() {
  if (!sessionId) return createSession();
  setProcessing(true);
  const response = await fetch(`/api/legacy-console/session/${sessionId}/reset`, { method: "POST" });
  const result = await response.json();
  terminal.textContent = "";
  print(result.output);
  setProcessing(false);
  command.focus();
}

clear.addEventListener("click", () => {
  terminal.textContent = "";
  command.focus();
});
help.addEventListener("click", () => {
  printHelp();
  command.focus();
});
reset.addEventListener("click", resetSession);

createSession();
