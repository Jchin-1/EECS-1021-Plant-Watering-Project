#!/bin/bash

# Script to delete the main branch from the repository
# This script helps automate the deletion of the main branch
# Run this script with appropriate permissions
#
# Usage: ./delete-main-branch.sh [REPO]
#   REPO: Optional repository in format owner/repo
#         Defaults to: Jchin-1/EECS-1021-Plant-Watering-Project

set -e

# Allow repository to be specified as argument or default to this repo
REPO="${1:-Jchin-1/EECS-1021-Plant-Watering-Project}"

echo "================================="
echo "Delete Main Branch Script"
echo "================================="
echo ""
echo "This script will help you delete the 'main' branch from:"
echo "  Repository: $REPO"
echo ""
echo "Prerequisites:"
echo "  1. You must have admin/owner permissions"
echo "  2. The default branch should be set to 'master'"
echo ""

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo "ERROR: GitHub CLI (gh) is not installed."
    echo "Please install it from: https://cli.github.com/"
    echo ""
    echo "Alternative: If you have git configured with authentication,"
    echo "you can delete the branch with:"
    echo "  git push origin --delete main"
    echo ""
    echo "Or use the GitHub web interface (see instructions in"
    echo "DELETE_MAIN_BRANCH_INSTRUCTIONS.md)"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "ERROR: Not authenticated with GitHub CLI."
    echo "Please run: gh auth login"
    exit 1
fi

echo "WARNING: This will permanently delete the 'main' branch!"
echo "All work should already be in the 'master' branch."
echo ""
read -p "Are you sure you want to continue? (yes/no): " confirm

# Convert to lowercase for case-insensitive comparison
confirm_lower=$(echo "$confirm" | tr '[:upper:]' '[:lower:]')

if [[ "$confirm_lower" != "yes" && "$confirm_lower" != "y" ]]; then
    echo "Deletion cancelled."
    exit 0
fi

echo ""
echo "Step 1: Setting 'master' as the default branch..."
gh repo edit "$REPO" --default-branch master || {
    echo "WARNING: Could not set default branch. Please set it manually in GitHub settings."
    read -p "Press Enter to continue once you've set 'master' as default branch..."
}

echo ""
echo "Step 2: Deleting the 'main' branch..."
gh api -X DELETE "/repos/$REPO/git/refs/heads/main" && {
    echo "SUCCESS: The 'main' branch has been deleted!"
    echo ""
    echo "Next steps:"
    echo "  1. Update local repositories: git fetch --prune"
    echo "  2. Delete local main branch: git branch -d main"
} || {
    echo "ERROR: Failed to delete the 'main' branch."
    echo "You may need to:"
    echo "  1. Check branch protection rules"
    echo "  2. Ensure you have admin permissions"
    echo "  3. Manually delete via GitHub web interface"
    exit 1
}
