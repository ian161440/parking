<template>
  <div class="auth-container">
    <h2>用户注册</h2>
    <form @submit.prevent="onSubmit">
      <div class="form-item">
        <label>用户名</label>
        <input v-model="form.username" />
      </div>
      <div class="form-item">
        <label>手机号</label>
        <input v-model="form.phone" />
      </div>
      <div class="form-item">
        <label>密码</label>
        <input type="password" v-model="form.password" />
      </div>
      <div class="form-item">
        <label>确认密码</label>
        <input type="password" v-model="confirmPassword" />
      </div>
      <div v-if="error" class="error">{{ error }}</div>
      <button type="submit" :disabled="loading">{{ loading ? '注册中...' : '注册' }}</button>
      <p class="switch">
        已有账号？
        <a href="" @click.prevent="goLogin">返回登录</a>
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
  phone: '',
  password: ''
});

const confirmPassword = ref('');
const loading = ref(false);
const error = ref('');

const onSubmit = async () => {
  if (!form.username || !form.password) {
    error.value = '用户名和密码不能为空';
    return;
  }
  if (form.password.length < 6) {
    error.value = '密码长度至少为6位';
    return;
  }
  if (form.password !== confirmPassword.value) {
    error.value = '两次输入的密码不一致';
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const res = await http.post('/api/auth/register', form);
    if (res.data && res.data.success) {
      router.push('/login');
    } else {
      error.value = res.data.message || '注册失败';
    }
  } catch (e) {
    error.value = e.response && e.response.data && e.response.data.message ? e.response.data.message : '注册失败';
  } finally {
    loading.value = false;
  }
};

const goLogin = () => {
  router.push('/login');
};
</script>
