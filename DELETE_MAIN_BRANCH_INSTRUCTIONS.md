# Instructions to Delete the Main Branch

## Overview
This repository has two primary branches:
- **main**: Contains only the initial commit (a2d4924)
- **master**: Contains all the actual project work (2 commits)

As requested, the `main` branch should be deleted since all work is in the `master` branch.

## Prerequisites
- You must have admin/owner permissions on the repository
- Ensure the default branch is set to `master` before deleting `main`

## Steps to Delete Main Branch

### Option 1: Using GitHub Web Interface (Recommended)

1. **Set master as the default branch:**
   - Go to https://github.com/Jchin-1/EECS-1021-Plant-Watering-Project
   - Click on **Settings** tab
   - Click on **Branches** in the left sidebar
   - Under "Default branch", click the switch icon
   - Select `master` from the dropdown
   - Click **Update** and confirm the change

2. **Delete the main branch:**
   - Go to https://github.com/Jchin-1/EECS-1021-Plant-Watering-Project/branches
   - Find the `main` branch in the list
   - Click the trash/delete icon next to it
   - Confirm the deletion

### Option 2: Using GitHub CLI (gh)

```bash
# Set master as default branch
gh repo edit Jchin-1/EECS-1021-Plant-Watering-Project --default-branch master

# Delete the main branch
gh api -X DELETE /repos/Jchin-1/EECS-1021-Plant-Watering-Project/git/refs/heads/main
```

### Option 3: Using Git Command Line

```bash
# Delete the remote main branch
git push origin --delete main
```

**Note:** This requires push permissions to the repository.

## Verification

After deletion, verify by running:
```bash
git ls-remote --heads origin
```

You should only see `master` and any PR branches (like `copilot/delete-main-branch`), but not `main`.

## Important Notes

- **Default Branch:** Make sure to change the default branch to `master` BEFORE deleting `main`, otherwise GitHub might prevent the deletion or cause issues with cloning.
- **Protected Branches:** If `main` is protected, you'll need to remove branch protection rules before deletion.
- **Pull Requests:** Any open PRs targeting `main` will need to be retargeted to `master`.

## Current Branch Status

As of this analysis:
- `main` branch commit: a2d4924 "Initial commit"
- `master` branch latest commit: c7a6382 "Add build configuration and project documentation"
- No files or work will be lost by deleting `main` since it only contains the initial commit

## After Deletion

Once `main` is deleted:
1. All contributors should update their local repositories:
   ```bash
   git fetch --prune
   git branch -d main  # Delete local main branch
   ```
2. Update any CI/CD configurations that reference `main`
3. Update any documentation that mentions `main` as the default branch
