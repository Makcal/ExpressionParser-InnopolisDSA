# ExpressionParser-InnopolisDSA

You are given mathematical expressions specified in infix notation with two prefix functions (min
 and max
). Your task is to convert it to a postfix notation (also known as Reverse Polish notation) using shunting yard algorithm.

Algorithm's details: [link](https://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC_%D1%81%D0%BE%D1%80%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%BE%D1%87%D0%BD%D0%BE%D0%B9_%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D0%B8)

## Input
The single line of input contains correct mathematical expression. The expression contains only

- single-digit decimal number (e.g. `0, 5, 9`),
- subtraction (`-`), division (`/`), multiplication (`*`), addition (`+`) operators,
- left and right parentheses,
- maximum and minimum functions with two arguments: `max ( <arg1> , <arg2> )` and `min ( <arg1> , <arg2> )`
All tokens are separated by spaces.

## Output
Print converted expression. All tokens must be separated by spaces.

### Note
All operations are left-associative: `1 + 2 + 3` is the same as `(1 + 2) + 3`, yielding `1 2 + 3 +` and not `1 2 3 + +`.
