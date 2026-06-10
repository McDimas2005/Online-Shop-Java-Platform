const statusEl = document.getElementById("status");
const barEl = document.getElementById("progressBar");
const textEl = document.getElementById("progressText");
const eventsEl = document.getElementById("events");

if (window.EventSource && orderId) {
  const source = new EventSource(`/sse/orders/${orderId}`);
  source.onmessage = event => {
    const payload = JSON.parse(event.data);
    statusEl.textContent = payload.status;
    barEl.style.width = `${payload.progressPercent}%`;
    textEl.textContent = `${payload.progressPercent}% complete`;
    const row = document.createElement("p");
    row.textContent = `${payload.status} - ${payload.progressPercent}% - ${payload.message}`;
    eventsEl.prepend(row);
  };
}
