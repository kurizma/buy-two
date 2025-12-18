// eslint.config.js
const angularRecommended = require('@angular-eslint/eslint-plugin').configs.recommended;
const prettierRecommended = require('eslint-plugin-prettier').configs.recommended;
const tsParser = require('@typescript-eslint/parser');

module.exports = [
  {
    files: ["src/**/*.{ts,js}"],
    ignores: ["node_modules/**", "dist/**"],

    languageOptions: {
      // Set the parser here!
        parser: tsParser,
      // Optional: add parserOptions if needed
        parserOptions: {
            ecmaVersion: 2020,
            sourceType: "module",
            project: [
                "./tsconfig.app.json",
                "./tsconfig.spec.json"
            ]      
        }
    },
    // Plugins and rules (as before)
    plugins: {
      "@angular-eslint": require("@angular-eslint/eslint-plugin"),
      "prettier": require("eslint-plugin-prettier")
    },
    rules: {
      ...angularRecommended.rules,
      ...prettierRecommended.rules,
      "prettier/prettier": "error",
      "no-unused-vars": "warn"
    }
  }
];

