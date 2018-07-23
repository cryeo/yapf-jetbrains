# yapf
[YAPF](https://github.com/google/yapf) plugin for Jetbrains IDEs.

## Getting Started

### Prerequisites
- You should install [YAPF](https://github.com/google/yapf) before using this plugin.
- You should know the path of YAPF executable.

### Installing
- Find `YAPF` in `Preferences` > `Plugins` > `Browse Repositories' on your Jetbrains IDE.
- Install it!

### Setting
You can set following settings in `Preferences` > `YAPF`.
- Format on save
- YAPF executable path (default: `/usr/local/bin/yapf`)
- YAPF style file name (default: `.style.yapf`)

Note that this plugin passes style file to YAPF in the following order:
1. `PROJECT_ROOT/[style_file_name]`
2. `VERSION_CONTROL_SYSTEM_ROOT/[style_file_name]`
3. `PROJECT_ROOT/.style.yapf`
4. `VERSION_CONTROL_SYSTEM_ROOT/.style.yapf`
5. No style option
   
## TODO
- Write unit tests

## License
This project is licensed under the MIT License.

