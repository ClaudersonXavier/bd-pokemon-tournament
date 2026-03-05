import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../../core/services/auth.service';
import { AppRoutes, REDIRECT_DELAY_MS, NAME_MIN_LENGTH, TRAINER_AVATAR_KEY } from '../../../core/constants';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit {
  currentUser: User | null = null;
  isSaving = false;
  saveSuccess: string | null = null;
  trainerOptions: string[] = [
    'unknown.png',
    'alain.png',
    'ash.png',
    'beauty.png',
    'chase.png',
    'elaine.png',
    'heroine-conquest.png',
    'lass.png',
    'nate.png',
    'rosa.png',
    'shadowtriad.png'
  ];
  selectedTrainer = 'unknown.png';
  profileForm;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.profileForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(NAME_MIN_LENGTH)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUser();

    if (!this.currentUser) {
      this.router.navigate([AppRoutes.LOGIN]);
      return;
    }

    this.profileForm.patchValue({
      name: this.currentUser.nome,
      email: this.currentUser.email
    });

    this.loadSelectedTrainer();
  }

  selectTrainer(trainerFile: string): void {
    this.selectedTrainer = trainerFile;
  }

  isTrainerSelected(trainerFile: string): boolean {
    return this.selectedTrainer === trainerFile;
  }

  getSelectedTrainerPath(): string {
    return `/images/trainers/${this.selectedTrainer}`;
  }

  saveProfile(): void {
    if (this.profileForm.invalid || !this.currentUser) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    this.saveSuccess = null;

    const updatedUser: User = {
      id: this.currentUser.id,
      nome: this.profileForm.value.name || this.currentUser.nome,
      email: this.profileForm.value.email || this.currentUser.email,
      admin: this.currentUser.admin,
    };

    this.authService.setAuthUser(updatedUser);
    localStorage.setItem(TRAINER_AVATAR_KEY(updatedUser.id || updatedUser.email || 'default'), this.selectedTrainer);

    this.isSaving = false;
    this.saveSuccess = 'Perfil atualizado com sucesso!';

    setTimeout(() => {
      this.router.navigate([AppRoutes.HOME]);
    }, REDIRECT_DELAY_MS);
  }

  cancel(): void {
    this.router.navigate([AppRoutes.HOME]);
  }

  private loadSelectedTrainer(): void {
    if (!this.currentUser) {
      return;
    }

    const savedTrainer = localStorage.getItem(TRAINER_AVATAR_KEY(this.currentUser.id || this.currentUser.email || 'default'));
    if (savedTrainer && this.trainerOptions.includes(savedTrainer)) {
      this.selectedTrainer = savedTrainer;
    }
  }
}
