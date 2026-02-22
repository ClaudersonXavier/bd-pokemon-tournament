import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, User } from '../../core/services/auth.service';

type Tab = 'tournaments' | 'profile' | 'admin';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  currentUser: User | null = null;
  activeTab: Tab = 'tournaments';
  isAdmin = false;
  selectedTrainer = 'unknown.png';
  showStatistics = false;
  showUserManagement = false;
  showCreateTournament = false;
  showChangePassword = false;
  searchTerm = '';
  filterStatus = 'all';
  filterRole = 'all';
  currentPage = 1;
  itemsPerPage = 10;
  selectedUser: any = null;
  showEditModal = false;
  showDeleteModal = false;
  userToDelete: any = null;

  // Alterar Senha
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  // Criar Torneio
  newTournament = {
    name: '',
    description: '',
    startDate: '',
    endDate: '',
    maxParticipants: 16,
    minLevel: 1,
    maxLevel: 100,
    allowedTypes: [] as string[],
    organizerId: 1,
    status: 'SCHEDULED',
    prizePool: 10000,
    registrationDeadline: ''
  };

  // Mock data para dropdowns (simulando SELECT queries)
  availableOrganizers = [
    { id: 1, name: 'Admin Principal', tournaments: 12 },
    { id: 2, name: 'Ash Ketchum', tournaments: 8 },
    { id: 3, name: 'Professor Oak', tournaments: 15 },
    { id: 4, name: 'Misty Waters', tournaments: 5 }
  ];

  pokemonTypes = [
    'Normal', 'Fogo', 'Água', 'Elétrico', 'Planta', 'Gelo',
    'Lutador', 'Venenoso', 'Terrestre', 'Voador', 'Psíquico',
    'Inseto', 'Pedra', 'Fantasma', 'Dragão', 'Sombrio',
    'Metálico', 'Fada'
  ];

  // Mock data para demonstração das queries de BD
  mockStats = {
    overview: {
      totalUsers: 1247,
      totalTournaments: 38,
      totalBattles: 5621,
      avgBattlesPerDay: 47
    },
    topTrainers: [
      { position: 1, name: 'Ash Ketchum', battles: 156, victories: 128, winRate: 82.05 },
      { position: 2, name: 'Misty Waters', battles: 142, victories: 115, winRate: 80.99 },
      { position: 3, name: 'Brock Stone', battles: 138, victories: 109, winRate: 78.99 },
      { position: 4, name: 'Gary Oak', battles: 134, victories: 105, winRate: 78.36 },
      { position: 5, name: 'May Haruka', battles: 129, victories: 98, winRate: 75.97 },
      { position: 6, name: 'Dawn Hikari', battles: 125, victories: 94, winRate: 75.20 },
      { position: 7, name: 'Serena Yvonne', battles: 121, victories: 89, winRate: 73.55 },
      { position: 8, name: 'Cilan Dent', battles: 118, victories: 86, winRate: 72.88 },
      { position: 9, name: 'Iris Dragon', battles: 115, victories: 83, winRate: 72.17 },
      { position: 10, name: 'Clemont Citron', battles: 112, victories: 80, winRate: 71.43 }
    ],
    popularPokemons: [
      { name: 'Pikachu', type: 'Elétrico', timesUsed: 892, winRate: 68.5 },
      { name: 'Charizard', type: 'Fogo', timesUsed: 756, winRate: 72.3 },
      { name: 'Blastoise', type: 'Água', timesUsed: 698, winRate: 70.1 },
      { name: 'Venusaur', type: 'Planta', timesUsed: 645, winRate: 69.8 },
      { name: 'Gengar', type: 'Fantasma', timesUsed: 587, winRate: 74.2 },
      { name: 'Dragonite', type: 'Dragão', timesUsed: 534, winRate: 76.5 },
      { name: 'Gyarados', type: 'Água', timesUsed: 512, winRate: 71.9 },
      { name: 'Alakazam', type: 'Psíquico', timesUsed: 489, winRate: 73.1 }
    ],
    tournaments: [
      { name: 'Copa Kanto 2026', participants: 64, battles: 127, status: 'Em Andamento', rank: 1 },
      { name: 'Liga Johto Classic', participants: 48, battles: 94, status: 'Finalizado', rank: 2 },
      { name: 'Torneio Hoenn Masters', participants: 32, battles: 63, status: 'Em Andamento', rank: 3 },
      { name: 'Desafio Sinnoh', participants: 24, battles: 47, status: 'Agendado', rank: 4 }
    ],
    monthlyGrowth: [
      { month: 'Fev/2026', newUsers: 87, total: 1247 },
      { month: 'Jan/2026', newUsers: 94, total: 1160 },
      { month: 'Dez/2025', newUsers: 102, total: 1066 },
      { month: 'Nov/2025', newUsers: 78, total: 964 },
      { month: 'Out/2025', newUsers: 91, total: 886 },
      { month: 'Set/2025', newUsers: 85, total: 795 }
    ],
    typeMatchups: [
      { type1: 'Fogo', type2: 'Planta', battles: 234, type1Wins: 189, type1Percentage: 80.77 },
      { type1: 'Água', type2: 'Fogo', battles: 218, type1Wins: 175, type1Percentage: 80.28 },
      { type1: 'Elétrico', type2: 'Água', battles: 203, type1Wins: 162, type1Percentage: 79.80 },
      { type1: 'Planta', type2: 'Água', battles: 197, type1Wins: 156, type1Percentage: 79.19 },
      { type1: 'Dragão', type2: 'Dragão', battles: 145, type1Wins: 73, type1Percentage: 50.34 }
    ]
  };

  // Mock data para gerenciar usuários
  mockUsers = [
    { id: 1, name: 'Ash Ketchum', email: 'ash@pokemon.com', role: 'ADMIN', active: true, createdAt: '2025-01-15', battles: 156, victories: 128, winRate: 82.05 },
    { id: 2, name: 'Misty Waters', email: 'misty@pokemon.com', role: 'USER', active: true, createdAt: '2025-02-10', battles: 142, victories: 115, winRate: 80.99 },
    { id: 3, name: 'Brock Stone', email: 'brock@pokemon.com', role: 'USER', active: true, createdAt: '2025-01-20', battles: 138, victories: 109, winRate: 78.99 },
    { id: 4, name: 'Gary Oak', email: 'gary@pokemon.com', role: 'USER', active: true, createdAt: '2025-03-05', battles: 134, victories: 105, winRate: 78.36 },
    { id: 5, name: 'May Haruka', email: 'may@pokemon.com', role: 'USER', active: false, createdAt: '2024-12-01', battles: 129, victories: 98, winRate: 75.97 },
    { id: 6, name: 'Dawn Hikari', email: 'dawn@pokemon.com', role: 'USER', active: true, createdAt: '2025-02-14', battles: 125, victories: 94, winRate: 75.20 },
    { id: 7, name: 'Serena Yvonne', email: 'serena@pokemon.com', role: 'USER', active: true, createdAt: '2025-01-28', battles: 121, victories: 89, winRate: 73.55 },
    { id: 8, name: 'Cilan Dent', email: 'cilan@pokemon.com', role: 'ADMIN', active: true, createdAt: '2025-02-01', battles: 118, victories: 86, winRate: 72.88 },
    { id: 9, name: 'Iris Dragon', email: 'iris@pokemon.com', role: 'USER', active: false, createdAt: '2024-11-15', battles: 115, victories: 83, winRate: 72.17 },
    { id: 10, name: 'Clemont Citron', email: 'clemont@pokemon.com', role: 'USER', active: true, createdAt: '2025-02-20', battles: 112, victories: 80, winRate: 71.43 },
    { id: 11, name: 'Bonnie Eureka', email: 'bonnie@pokemon.com', role: 'USER', active: true, createdAt: '2025-03-01', battles: 85, victories: 58, winRate: 68.24 },
    { id: 12, name: 'Tracey Sketch', email: 'tracey@pokemon.com', role: 'USER', active: true, createdAt: '2025-01-10', battles: 92, victories: 61, winRate: 66.30 }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get current user from auth service
    const userSignal = this.authService.currentUser();
    this.currentUser = userSignal;

    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    // Check if user is admin (mock - in real app, would come from backend)
    this.isAdmin = this.currentUser.email?.includes('admin') || false;
    this.loadSelectedTrainer();
  }

  setActiveTab(tab: Tab): void {
    this.activeTab = tab;
  }

  logout(): void {
    this.authService.logout();
  }

  isTabActive(tab: Tab): boolean {
    return this.activeTab === tab;
  }

  toggleUserManagement(): void {
    this.showUserManagement = !this.showUserManagement;
  }

  closeUserManagement(): void {
    this.showUserManagement = false;
    this.searchTerm = '';
    this.filterStatus = 'all';
    this.filterRole = 'all';
    this.currentPage = 1;
  }

  get filteredUsers() {
    return this.mockUsers.filter(user => {
      const matchesSearch = user.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                          user.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesStatus = this.filterStatus === 'all' || 
                          (this.filterStatus === 'active' && user.active) ||
                          (this.filterStatus === 'inactive' && !user.active);
      const matchesRole = this.filterRole === 'all' || user.role === this.filterRole;
      
      return matchesSearch && matchesStatus && matchesRole;
    });
  }

  get paginatedUsers() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(start, start + this.itemsPerPage);
  }

  get totalPages() {
    return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  get userOverview() {
    return {
      totalActive: this.mockUsers.filter(u => u.active).length,
      totalInactive: this.mockUsers.filter(u => !u.active).length,
      totalAdmins: this.mockUsers.filter(u => u.role === 'ADMIN').length,
      newThisWeek: this.mockUsers.filter(u => new Date(u.createdAt) > new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)).length
    };
  }

  editUser(user: any): void {
    this.selectedUser = { ...user };
    this.showEditModal = true;
  }

  saveUser(): void {
    const index = this.mockUsers.findIndex(u => u.id === this.selectedUser.id);
    if (index !== -1) {
      this.mockUsers[index] = { ...this.selectedUser };
    }
    this.closeEditModal();
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedUser = null;
  }

  confirmDelete(user: any): void {
    this.userToDelete = user;
    this.showDeleteModal = true;
  }

  deleteUser(): void {
    const index = this.mockUsers.findIndex(u => u.id === this.userToDelete.id);
    if (index !== -1) {
      this.mockUsers[index].active = false;
    }
    this.closeDeleteModal();
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.userToDelete = null;
  }

  toggleUserStatus(user: any): void {
    user.active = !user.active;
  }

  promoteUser(user: any): void {
    user.role = user.role === 'ADMIN' ? 'USER' : 'ADMIN';
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filterStatus = 'all';
    this.filterRole = 'all';
    this.currentPage = 1;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  // Create Tournament methods
  toggleCreateTournament(): void {
    this.showCreateTournament = !this.showCreateTournament;
    if (this.showCreateTournament) {
      this.resetTournamentForm();
    }
  }

  closeCreateTournament(): void {
    this.showCreateTournament = false;
    this.resetTournamentForm();
  }

  resetTournamentForm(): void {
    this.newTournament = {
      name: '',
      description: '',
      startDate: '',
      endDate: '',
      maxParticipants: 16,
      minLevel: 1,
      maxLevel: 100,
      allowedTypes: [],
      organizerId: 1,
      status: 'SCHEDULED',
      prizePool: 10000,
      registrationDeadline: ''
    };
  }

  togglePokemonType(type: string): void {
    const index = this.newTournament.allowedTypes.indexOf(type);
    if (index > -1) {
      this.newTournament.allowedTypes.splice(index, 1);
    } else {
      this.newTournament.allowedTypes.push(type);
    }
  }

  isTypeSelected(type: string): boolean {
    return this.newTournament.allowedTypes.includes(type);
  }

  saveTournament(): void {
    // Simulação de INSERT INTO tournaments
    // Na versão real, isso seria uma chamada HTTP POST para o backend
    console.log('🗄️ SQL Simulado:', `
      INSERT INTO tournaments (
        name, description, start_date, end_date, max_participants,
        min_level, max_level, organizer_id, status, prize_pool,
        registration_deadline, created_at
      ) VALUES (
        '${this.newTournament.name}',
        '${this.newTournament.description}',
        '${this.newTournament.startDate}',
        '${this.newTournament.endDate}',
        ${this.newTournament.maxParticipants},
        ${this.newTournament.minLevel},
        ${this.newTournament.maxLevel},
        ${this.newTournament.organizerId},
        '${this.newTournament.status}',
        ${this.newTournament.prizePool},
        '${this.newTournament.registrationDeadline}',
        NOW()
      );
    `);

    // Simular INSERT INTO tournament_allowed_types (relação N:N)
    if (this.newTournament.allowedTypes.length > 0) {
      console.log('🗄️ SQL Simulado (Tipos Permitidos):', `
        INSERT INTO tournament_allowed_types (tournament_id, type_name)
        VALUES
        ${this.newTournament.allowedTypes.map(type => 
          `(LAST_INSERT_ID(), '${type}')`
        ).join(',\n        ')};
      `);
    }

    alert(`✅ Torneio "${this.newTournament.name}" criado com sucesso!\n\nDados salvos no banco de dados (simulação).`);
    this.closeCreateTournament();
  }

  get isTournamentFormValid(): boolean {
    return !!(
      this.newTournament.name.trim() &&
      this.newTournament.description.trim() &&
      this.newTournament.startDate &&
      this.newTournament.endDate &&
      this.newTournament.registrationDeadline &&
      this.newTournament.maxParticipants > 0 &&
      this.newTournament.minLevel >= 1 &&
      this.newTournament.maxLevel <= 100 &&
      this.newTournament.minLevel < this.newTournament.maxLevel
    );
  }

  getSelectedTrainerPath(): string {
    return `/images/trainers/${this.selectedTrainer}`;
  }

  goToEditProfile(): void {
    this.router.navigate(['/edit-profile']);
  }

  toggleStatistics(): void {
    this.showStatistics = !this.showStatistics;
  }

  closeStatistics(): void {
    this.showStatistics = false;
  }

  // Change Password methods
  openChangePassword(): void {
    this.showChangePassword = true;
    this.resetPasswordForm();
  }

  closeChangePassword(): void {
    this.showChangePassword = false;
    this.resetPasswordForm();
  }

  resetPasswordForm(): void {
    this.passwordForm = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  }

  saveNewPassword(): void {
    // Validações
    if (!this.isPasswordFormValid) {
      alert('❌ Por favor, preencha todos os campos corretamente!');
      return;
    }

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      alert('❌ As senhas não coincidem!');
      return;
    }

    if (this.passwordForm.newPassword.length < 6) {
      alert('❌ A nova senha deve ter pelo menos 6 caracteres!');
      return;
    }

    // Simulação de UPDATE no banco de dados
    console.log('🗄️ SQL Simulado:', `
      UPDATE users 
      SET password = PASSWORD_HASH('${this.passwordForm.newPassword}'),
          updated_at = NOW()
      WHERE id = ${this.currentUser?.id}
        AND password = PASSWORD_HASH('${this.passwordForm.currentPassword}');
        
      -- Verifica se a senha atual está correta
      -- Se rows_affected = 0, senha atual incorreta
      -- Se rows_affected = 1, senha alterada com sucesso
    `);

    alert(`✅ Senha alterada com sucesso!\n\nConcepts demonstrados:\n- UPDATE com WHERE multiple conditions\n- PASSWORD_HASH para segurança\n- Timestamp automático (updated_at)\n- Verificação de senha antiga`);
    this.closeChangePassword();
  }

  get isPasswordFormValid(): boolean {
    return !!(
      this.passwordForm.currentPassword.trim() &&
      this.passwordForm.newPassword.trim() &&
      this.passwordForm.confirmPassword.trim()
    );
  }

  get passwordStrength(): string {
    const password = this.passwordForm.newPassword;
    if (!password) return 'none';
    if (password.length < 6) return 'weak';
    if (password.length < 10) return 'medium';
    return 'strong';
  }

  private loadSelectedTrainer(): void {
    const savedTrainer = localStorage.getItem(this.getTrainerStorageKey());
    if (savedTrainer) {
      this.selectedTrainer = savedTrainer;
    }
  }

  private getTrainerStorageKey(): string {
    const userId = this.currentUser?.id || this.currentUser?.email || 'default';
    return `trainer-avatar-${userId}`;
  }
}
