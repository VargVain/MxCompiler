void print(char *s) { printf("%s", s); }

void println(char *s) { printf("%s\n", s); }

void printInt(int x) { printf("%d", x); }

void printlnInt(int x) { printf("%d\n", x); }

char *getString() {
  char *s = malloc(1 << 8);
  scanf("%s", s);
  return s;
}

int getInt() {
  int x;
  scanf("%d", &x);
  return x;
}

char *toString(int x) {
  char *s = malloc(1 << 4);
  sprintf(s, "%d", x);
  return s;
}

char *string_substring(char *s, int l, int r) {
  char *t = malloc(r - l + 1);
  for (int i = l; i < r; i++) t[i - l] = s[i];
  t[r - l] = '\0';
  return t;
}

int string_parseInt(char *s) {
  int x;
  sscanf(s, "%d", &x);
  return x;
}

int string_ord(char *s, int x) { return s[x]; }

char *string_add(char *s, char *t) {
  char *p = malloc(strlen(s) + strlen(t) + 1);
  strcpy(p, s);
  strcat(p, t);
  return p;
}

unsigned char string_lt(char *s, char *t) { return strcmp(s, t) < 0; }

unsigned char string_le(char *s, char *t) { return strcmp(s, t) <= 0; }

unsigned char string_gt(char *s, char *t) { return strcmp(s, t) > 0; }

unsigned char string_ge(char *s, char *t) { return strcmp(s, t) >= 0; }

unsigned char string_eq(char *s, char *t) { return strcmp(s, t) == 0; }

unsigned char string_ne(char *s, char *t) { return strcmp(s, t) != 0; }