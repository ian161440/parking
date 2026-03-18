<template>
  <div class="auth-container">
    <h2>用户登录</h2>
    <form @submit.prevent="onSubmit">
      <div class="form-item">
        <label>用户名</label>
        <input v-model="form.username" autocomplete="username" />
      </div>
      <div class="form-item">
        <label>密码</label>
        <input type="password" v-model="form.password" autocomplete="current-password" />
      </div>
      <div v-if="error" class="error">{{ error }}</div>
      <button type="submit" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      <p class="switch">
        还没有账号？
        <a href="" @click.prevent="goRegister">立即注册</a>
      </p>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import http from '../api/http';

const router = useRouter();

const form = reactive({
  username: '',
  password: ''
});

const loading = ref(false);
const error = ref('');

const onSubmit = async () => {
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码';
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const res = await http.post('/api/auth/login', form);
    if (res.data && res.data.success) {
      const user = res.data.data;
      localStorage.setItem('user', JSON.stringify(user));
      if (user.role === 'ADMIN') {
        router.push('/admin');
      } else {
        router.push('/user');
      }
    } else {
      error.value = res.data.message || '登录失败';
    }
  } catch (e) {
    error.value = e.response && e.response.data && e.response.data.message ? e.response.data.message : '登录失败';
  } finally {
    loading.value = false;
  }
};

const goRegister = () => {
  router.push('/register');
};
</script>
