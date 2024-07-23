const form = document.getElementById('login-form');

form.addEventListener('submit', event => {
  event.preventDefault();

  const formData = new FormData(event.target);
  let member  = {};
  for (const entry of formData.entries()) {
    const [key, value] = entry;
    member[key] = value;
  }

  loginMember(member);
})

const loginMember = (member) => {
  const jsonData = JSON.stringify(member);

  axios.request({
    url: '/api/members/login',
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
    },
    data: jsonData
  }).then((response) => {
    const authToken = response.headers['authorization']; // 수정된 부분
    if (authToken) {
      localStorage.setItem('authToken', authToken);
      window.location.href = '/check'; // 수정된 부분
    } else {
      console.error('Authorization token not found in the response headers.');
    }
  }).catch((error) => {
    console.error('Error during login:', error);
  });
}

