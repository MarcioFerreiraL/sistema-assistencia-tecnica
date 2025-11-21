import { reactive } from 'vue';

// Estado global reativo
const state = reactive({
  items: []
});

export function useToast() {
  const remove = (id) => {
    const index = state.items.findIndex(t => t.id === id);
    if (index > -1) state.items.splice(index, 1);
  };

  const add = (message, type = 'info', duration = 4000) => {
    const id = Date.now() + Math.random();
    state.items.push({ id, message, type });
    
    if (duration) {
      setTimeout(() => remove(id), duration);
    }
  };

  return {
    toasts: state.items,
    success: (msg) => add(msg, 'success'),
    error: (msg) => add(msg, 'error'),
    warning: (msg) => add(msg, 'warning'),
    info: (msg) => add(msg, 'info'),
    remove
  };
}