# Remove .env files
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch .env" --prune-empty --tag-name-filter cat -- --all

# Remove any potential credential files
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch *credentials*.json" --prune-empty --tag-name-filter cat -- --all

# Remove any config files that might contain keys
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch config*.json" --prune-empty --tag-name-filter cat -- --all

# Force garbage collection and remove old refs
git for-each-ref --format="delete %(refname)" refs/original | git update-ref --stdin
git reflog expire --expire=now --all
git gc --prune=now --aggressive
