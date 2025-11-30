# Vite + TypeScript Migration Guide

## What's Been Created

I've set up a new **Vite + TypeScript** project in `frontend-vite/` with:

✅ Vite configuration
✅ TypeScript setup with strict mode
✅ React 18 with modern patterns
✅ ThemeContext converted to TypeScript
✅ Navbar component as TypeScript example
✅ App structure ready

## Why This Upgrade?

### Performance Benefits:
- **Dev Server**: Starts in ~200ms (vs 10-30s with CRA)
- **HMR**: Instant hot reload
- **Build Time**: 5-10x faster
- **Bundle Size**: Smaller optimized bundles

### Developer Experience:
- **TypeScript**: Catch errors before runtime
- **Better IDE Support**: Autocomplete, refactoring
- **Modern Tooling**: ESM, faster everything
- **Industry Standard**: Vite is the modern choice

## Quick Start

```bash
cd frontend-vite
npm install
npm run dev
```

The dev server will start at http://localhost:3000

## Migration Steps

### 1. Install Dependencies
```bash
cd frontend-vite
npm install
```

### 2. Copy Component Files

For each component in `frontend/src/components/`:

```bash
# Example for Skills component
cp ../frontend/src/components/Skills.js src/components/Skills.tsx
cp ../frontend/src/components/Skills.css src/components/
```

### 3. Convert to TypeScript

Change each `.js` file to `.tsx` and update:

**Before (JavaScript):**
```javascript
import React, { useState } from 'react';

function Skills() {
  const [active, setActive] = useState(false);
  // ...
}
```

**After (TypeScript):**
```typescript
import { useState } from 'react'

function Skills() {
  const [active, setActive] = useState<boolean>(false)
  // ...
}
```

### Key Changes:

1. **Remove `React` import** - Not needed in React 17+
2. **Add types** to function parameters and state
3. **Use `import.meta.env`** instead of `process.env`
4. **Remove semicolons** (optional, but cleaner)

### 4. Type Examples

```typescript
// Props interface
interface ButtonProps {
  label: string
  onClick: () => void
  disabled?: boolean  // optional
}

function Button({ label, onClick, disabled = false }: ButtonProps) {
  return <button onClick={onClick} disabled={disabled}>{label}</button>
}

// State with type
const [count, setCount] = useState<number>(0)
const [user, setUser] = useState<User | null>(null)

// Event handlers
const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
  console.log(e.currentTarget)
}

const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  setValue(e.target.value)
}
```

## Component Migration Checklist

- [ ] Hero.tsx
- [ ] TechShowcase.tsx
- [ ] Skills.tsx
- [ ] Experience.tsx
- [ ] Projects.tsx
- [ ] Contact.tsx
- [x] Navbar.tsx (done as example)

## Environment Variables

**Old (CRA):**
```
REACT_APP_API_URL=http://localhost:5000
```

**New (Vite):**
```
VITE_API_URL=http://localhost:5000
```

Access with: `import.meta.env.VITE_API_URL`

## Docker Update

Update `frontend/Dockerfile` to use Vite:

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY frontend-vite/package*.json ./
RUN npm ci
COPY frontend-vite/ ./
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Testing the Migration

1. **Start dev server:**
   ```bash
   cd frontend-vite
   npm run dev
   ```

2. **Check for errors** in browser console

3. **Test all features:**
   - Navigation
   - Dark mode toggle
   - Animations
   - API calls

4. **Build for production:**
   ```bash
   npm run build
   npm run preview
   ```

## Common Issues & Solutions

### Issue: Module not found
**Solution:** Check import paths, ensure file extensions are correct

### Issue: Type errors
**Solution:** Add proper TypeScript types or use `any` temporarily

### Issue: Environment variables not working
**Solution:** Use `VITE_` prefix and `import.meta.env`

### Issue: CSS not loading
**Solution:** Ensure CSS files are copied and imported correctly

## Benefits You'll See

1. **Instant feedback** - Changes reflect immediately
2. **Type safety** - Catch bugs before they happen
3. **Better refactoring** - IDE knows your code structure
4. **Smaller bundles** - Vite optimizes automatically
5. **Modern practices** - Industry-standard tooling

## Next Steps After Migration

1. Add **Vitest** for testing
2. Set up **ESLint** with TypeScript rules
3. Add **Prettier** for code formatting
4. Consider **React Query** for API calls
5. Add **Zod** for runtime validation

## Need Help?

- Vite Docs: https://vitejs.dev
- TypeScript Docs: https://www.typescriptlang.org
- React TypeScript Cheatsheet: https://react-typescript-cheatsheet.netlify.app

## Rollback Plan

If you need to go back to CRA:
1. Keep the `frontend/` directory as-is
2. The old setup still works
3. You can migrate gradually

---

**Pro Tip:** Start by migrating one component at a time. Get it working, then move to the next. The Navbar is already done as an example!
