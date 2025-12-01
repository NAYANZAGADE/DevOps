#!/bin/bash

echo "🌐 Opening your portfolio in the browser..."
echo ""
echo "✅ Frontend: http://localhost:3000"
echo "✅ Backend API: http://localhost:5000"
echo ""

# Try to open in default browser
if command -v xdg-open > /dev/null; then
    xdg-open http://localhost:3000
elif command -v gnome-open > /dev/null; then
    gnome-open http://localhost:3000
elif command -v open > /dev/null; then
    open http://localhost:3000
else
    echo "Please open http://localhost:3000 in your browser"
fi
