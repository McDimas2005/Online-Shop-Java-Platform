const terminal = document.getElementById("terminal");
const command = document.getElementById("command");
const send = document.getElementById("send");
const reset = document.getElementById("reset");
const statusEl = document.getElementById("sessionStatus");
let sessionId = null;
let stream = null;

function print(text) {
  terminal.textContent += text + "\n";
  terminal.scrollTop = terminal.scrollHeight;
}

async function createSession() {
  const response = await fetch("/api/legacy-console/session", { method: "POST" });
  const result = await response.json();
  sessionId = result.sessionId;
  terminal.textContent = "";
  print(result.output);
  statusEl.textContent = `Session ${sessionId}`;
  openStream();
}

function openStream() {
  if (stream) stream.close();
  stream = new EventSource(`/sse/legacy-console/${sessionId}`);
  stream.onmessage = event => {
    const result = JSON.parse(event.data);
    if (result.output) print(result.output);
  };
}

async function submitInput() {
  const input = command.value.trim();
  if (!sessionId || !input) return;
  print(`> ${input}`);
  command.value = "";
  await fetch(`/api/legacy-console/session/${sessionId}/input`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ input })
  });
}

send.addEventListener("click", submitInput);
command.addEventListener("keydown", event => {
  if (event.key === "Enter") submitInput();
});
reset.addEventListener("click", async () => {
  if (!sessionId) return createSession();
  const response = await fetch(`/api/legacy-console/session/${sessionId}/reset`, { method: "POST" });
  const result = await response.json();
  terminal.textContent = "";
  print(result.output);
});

createSession();
