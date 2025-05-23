// @ts-check

import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';
import functional from 'eslint-plugin-functional';

export default tseslint.config(
  {
    ignores: [
      'dist/**/*.ts',
      'dist/**',
      '**/*.mjs',
      '**/*.js',
      'notes/**/*.ts',
      'notes/**',
    ],
  },
  {
    languageOptions: {
      parserOptions: {
        projectService: true,
        tsconfigRootDir: import.meta.dirname,
      },
    },
  },
  eslint.configs.recommended,
  ...tseslint.configs.strictTypeChecked,
  ...tseslint.configs.stylisticTypeChecked,
  {
    plugins: {
      'functional': functional
    },
    rules: {
      'functional/type-declaration-immutability': ['error', {
        rules: [
          {
            identifiers: '^(?!I?Mutable).+',
            immutability: 'ReadonlyShallow',
            comparator: 'AtLeast',
            fixer: false,
            suggestions: false
          },
        ],
        ignoreInterfaces: false
      }],
    }
  }
);
