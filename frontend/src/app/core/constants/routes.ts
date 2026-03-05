export const AppRoutes = {
  HOME:         '/home',
  LOGIN:        '/login',
  REGISTER:     '/register',
  POKEMONS:     '/pokemons',
  TIMES:        '/times',
  RELATORIOS:   '/relatorios',
  EDIT_PROFILE: '/edit-profile',
  TOURNAMENT:   (id: number | string) => `/tournament/${id}`,
} as const;
