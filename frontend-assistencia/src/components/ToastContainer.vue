<template>
  <div class="toast-container">
    <transition-group name="toast">
      <div 
        v-for="toast in toasts" 
        :key="toast.id" 
        class="toast-item"
        :class="toast.type"
        @click="remove(toast.id)"
      >
        <div class="icon">
          <span v-if="toast.type === 'success'">✅</span>
          <span v-else-if="toast.type === 'error'">❌</span>
          <span v-else-if="toast.type === 'warning'">⚠️</span>
          <span v-else>ℹ️</span>
        </div>
        <p class="message">{{ toast.message }}</p>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useToast } from '../composables/useToast';
const { toasts, remove } = useToast();
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.toast-item {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 300px;
  padding: 16px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  border-left: 5px solid #ccc;
  animation: slideIn 0.3s ease;
}

.toast-item.success { border-left-color: var(--color-success); background: #f0fdf4; }
.toast-item.error { border-left-color: var(--color-danger); background: #fef2f2; }
.toast-item.warning { border-left-color: var(--color-warning); background: #fffbeb; }
.toast-item.info { border-left-color: var(--color-info); background: #eff6ff; }

.message { font-weight: 500; font-size: 0.95rem; color: var(--color-text-main); margin: 0; }

/* Animações Vue */
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from { opacity: 0; transform: translateX(30px); }
.toast-leave-to { opacity: 0; transform: translateY(-20px); }

@keyframes slideIn {
  from { opacity: 0; transform: translateX(30px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>