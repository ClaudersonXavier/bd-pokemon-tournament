import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, User } from '../../../core/services/auth.service';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
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
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUser();

    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.profileForm.patchValue({
      name: this.currentUser.name,
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
      name: this.profileForm.value.name || this.currentUser.name,
      email: this.profileForm.value.email || this.currentUser.email
    };

    this.authService.setAuthUser(updatedUser);
    localStorage.setItem(this.getTrainerStorageKey(updatedUser), this.selectedTrainer);

    this.isSaving = false;
    this.saveSuccess = 'Perfil atualizado com sucesso!';

    setTimeout(() => {
      this.router.navigate(['/home']);
    }, 1200);
  }

  cancel(): void {
    this.router.navigate(['/home']);
  }

  private loadSelectedTrainer(): void {
    if (!this.currentUser) {
      return;
    }

    const savedTrainer = localStorage.getItem(this.getTrainerStorageKey(this.currentUser));
    if (savedTrainer && this.trainerOptions.includes(savedTrainer)) {
      this.selectedTrainer = savedTrainer;
    }
  }

  private getTrainerStorageKey(user: User): string {
    const userId = user.id || user.email || 'default';
    return `trainer-avatar-${userId}`;
  }
}
