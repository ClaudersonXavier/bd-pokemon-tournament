import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './page-header.component.html',
  styleUrl: './page-header.component.css',
})
export class PageHeaderComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() logoSrc = '/icons/poke-ball.png';
  @Input() userName?: string;
  @Input() userEmail?: string;
  @Input() showBack = false;
  @Output() backClick = new EventEmitter<void>();

  onBack(): void {
    this.backClick.emit();
  }
}
