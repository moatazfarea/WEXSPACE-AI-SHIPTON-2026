#!/usr/bin/env bash
set -euo pipefail

ADB="${ADB:-adb}"
OUT="${1:-studio-os/build/raw-android-runtime.mp4}"
mkdir -p "$(dirname "$OUT")"

PKG="ai.wexspace.fielddesk.samsung.sandbox"
ACT="ai.wexspace.fielddesk.CommandCenterActivity"
REMOTE="/sdcard/wexspace-shipaton-r04.mp4"

tap_text() {
  local needle="$1"
  local max_tries="${2:-6}"
  local i
  for i in $(seq 1 "$max_tries"); do
    "$ADB" shell uiautomator dump /sdcard/window.xml >/dev/null 2>&1 || true
    "$ADB" pull /sdcard/window.xml /tmp/window.xml >/dev/null 2>&1 || true
    if NEEDLE="$needle" python3 - <<'PY'
import os,re,subprocess,sys,xml.etree.ElementTree as ET
needle=os.environ["NEEDLE"].lower()
try:
    root=ET.parse("/tmp/window.xml").getroot()
except Exception:
    sys.exit(2)
for n in root.iter("node"):
    text=((n.attrib.get("text") or "")+" "+(n.attrib.get("content-desc") or "")).strip()
    if needle in text.lower():
        m=re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]",n.attrib.get("bounds",""))
        if not m: continue
        x=(int(m.group(1))+int(m.group(3)))//2
        y=(int(m.group(2))+int(m.group(4)))//2
        subprocess.check_call(["adb","shell","input","tap",str(x),str(y)])
        print("TAP",needle,x,y)
        sys.exit(0)
sys.exit(1)
PY
    then
      return 0
    fi
    "$ADB" shell input swipe 540 1550 540 720 650 || true
    sleep 0.7
  done
  echo "Could not locate visible text: $needle" >&2
  return 1
}

back_to_top() {
  "$ADB" shell input swipe 540 700 540 1650 650 || true
  "$ADB" shell input swipe 540 700 540 1650 650 || true
}

run_mission() {
  local open_text="$1"
  tap_text "$open_text" 8
  sleep 3
  tap_text "ROUTE TO SPECIALIST" 5
  sleep 3
  tap_text "RUN DETERMINISTIC TOOL" 7
  sleep 4
  tap_text "RUN INDEPENDENT VERIFICATION" 7
  sleep 4
  tap_text "ISSUE EVIDENCE RECEIPT" 7
  sleep 4
  "$ADB" shell input swipe 540 1500 540 650 900 || true
  sleep 3
  "$ADB" shell input keyevent 4
  sleep 3
  back_to_top
  sleep 1
}

"$ADB" wait-for-device
"$ADB" shell wm size 1080x1920
"$ADB" shell wm density 420
"$ADB" shell settings put system show_touches 1 || true
"$ADB" shell settings put global window_animation_scale 0.7 || true
"$ADB" shell settings put global transition_animation_scale 0.7 || true
"$ADB" shell settings put global animator_duration_scale 0.7 || true

"$ADB" shell am force-stop "$PKG" || true
"$ADB" shell am start -W -n "$PKG/$ACT"
sleep 5

START=$(date +%s)
"$ADB" shell rm -f "$REMOTE" || true
"$ADB" shell screenrecord --bit-rate 16000000 --size 1080x1920 --time-limit 115 "$REMOTE" >/tmp/screenrecord.log 2>&1 &
CAP=$!
sleep 3

run_mission "OPEN PUMP PROJECT"
run_mission "OPEN SOLAR PROJECT"
run_mission "OPEN RESCUE PROJECT"

# Return to the command center, reveal the shared execution fabric and WEXSPACE Pro.
"$ADB" shell input swipe 540 1560 540 520 1000 || true
sleep 6
"$ADB" shell input swipe 540 1560 540 520 1000 || true
sleep 6

ELAPSED=$(( $(date +%s) - START ))
if [ "$ELAPSED" -lt 113 ]; then
  sleep $((113-ELAPSED))
fi

wait "$CAP" || true
"$ADB" pull "$REMOTE" "$OUT"
test -s "$OUT"
echo "RAW_RUNTIME=$OUT"
ffprobe -v error -show_entries format=duration,size -show_entries stream=width,height,avg_frame_rate -of json "$OUT"
