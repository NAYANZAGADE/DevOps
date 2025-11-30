#!/bin/bash

echo "🚀 Migrating Portfolio to Vite + TypeScript"
echo "==========================================="

# Create components directory
mkdir -p frontend-vite/src/components

# Copy CSS files
echo "📋 Copying CSS files..."
cp frontend/src/components/*.css frontend-vite/src/components/ 2>/dev/null || true

# Copy public assets
echo "📋 Copying public assets..."
cp -r frontend/public/* frontend-vite/public/ 2>/dev/null || mkdir -p frontend-vite/public

echo ""
echo "✅ Basic structure created!"
echo ""
echo "📝 Next steps:"
echo "1. cd frontend-vite"
echo "2. npm install"
echo "3. Copy component files from ../frontend/src/components/ to ./src/components/"
echo "4. Rename .js files to .tsx"
echo "5. Add TypeScript types to components"
echo "6. npm run dev"
echo ""
echo "💡 Vite will be MUCH faster than Create React App!"
