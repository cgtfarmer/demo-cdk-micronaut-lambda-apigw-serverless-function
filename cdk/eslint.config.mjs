// @ts-check

import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';
import stylisticPlugin from '@stylistic/eslint-plugin'
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
    ]
  },
  {
    languageOptions: {
      parserOptions: {
        projectService: true,
        tsconfigRootDir: import.meta.dirname,
      }
    }
  },
  eslint.configs.recommended,
  ...tseslint.configs.strictTypeChecked,
  ...tseslint.configs.stylisticTypeChecked,
  stylisticPlugin.configs.customize({
    flat: true,
    semi: true,
    braceStyle: '1tbs',
    commaDangle: 'never'
  }),
  {
    plugins: {
      '@stylistic': stylisticPlugin,
      'functional': functional
    },
    rules: {
      '@stylistic/max-len': ['error', { 'code': 100 }],
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
      }]
    }
  }
);
