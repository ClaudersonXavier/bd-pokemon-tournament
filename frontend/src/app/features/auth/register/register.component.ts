import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading = false;
  showPassword = false;
  showConfirmPassword = false;
  registerError: string | null = null;
  registerSuccess: string | null = null;
  private isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {
    this.registerForm = this.fb.group(
      {
        nome: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        senha: ['', [Validators.required, Validators.minLength(6)]],
        confirmarSenha: ['', [Validators.required]],
      },
      { validators: this.senhasIguaisValidator },
    );
  }

  senhasIguaisValidator(control: AbstractControl): ValidationErrors | null {
    const senha = control.get('senha');
    const confirmar = control.get('confirmarSenha');
    if (!senha || !confirmar) return null;
    return senha.value === confirmar.value ? null : { senhasDiferentes: true };
  }

  get nomeControl() {
    return this.registerForm.get('nome');
  }
  get emailControl() {
    return this.registerForm.get('email');
  }
  get senhaControl() {
    return this.registerForm.get('senha');
  }
  get confirmarSenhaControl() {
    return this.registerForm.get('confirmarSenha');
  }

  get togglePasswordLabel(): string {
    return this.showPassword ? 'Ocultar senha' : 'Mostrar senha';
  }
  get toggleConfirmPasswordLabel(): string {
    return this.showConfirmPassword
      ? 'Ocultar confirmação'
      : 'Mostrar confirmação';
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.registerError = 'Por favor, preencha todos os campos corretamente.';
      return;
    }

    // Prevenir submissões duplicadas
    if (this.isLoading || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.isLoading = true;
    this.registerError = null;
    this.registerSuccess = null;

    const { nome, email, senha } = this.registerForm.value;

    this.authService.registro({ nome, email, senha }).subscribe({
      next: () => {
        this.registerSuccess =
          'Cadastro realizado com sucesso! Redirecionando...';

        // Aguarda um momento para mostrar a mensagem de sucesso antes de redirecionar
        setTimeout(() => {
          this.isLoading = false;
          this.isSubmitting = false;
          // O AuthService já armazenou o token; redireciona direto para home
          this.router.navigate(['/home']);
        }, 1000);
      },
      error: (err) => {
        console.error('Erro no registro:', err);
        console.log('Status:', err.status);
        console.log('Mensagem:', err.message);
        this.isLoading = false;
        this.isSubmitting = false;
        if (err.status === 409) {
          this.registerError = 'Este e-mail já está cadastrado.';
        } else if (err.status === 403) {
          this.registerError = 'Acesso negado. Verifique se o backend está rodando na porta 8080.';
        } else {
          this.registerError = `Erro ao cadastrar (${err.status}): ${err.error?.erro || err.message || 'Tente novamente.'}`;
        }
      },
    });
  }
}
