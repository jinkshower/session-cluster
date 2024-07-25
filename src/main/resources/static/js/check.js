// Axios 인터셉터 설정
axios.interceptors.request.use(
    config => {
      const token = localStorage.getItem('authToken');
      if (token) {
        config.headers['Authorization'] = token;
      }
      return config;
    },
    error => {
      return Promise.reject(error);
    }
);

// 페이지 로드 시 사용자 정보 요청 및 표시
document.addEventListener('DOMContentLoaded', function() {
  axios.get('/api/members/check')
  .then(response => {
    const email = response.data.email;
    document.getElementById('user-email').textContent = email;
  })
  .catch(error => {
    console.error('Error fetching user info:', error);
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
  });
});
