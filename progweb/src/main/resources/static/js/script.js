document.addEventListener('DOMContentLoaded', function () {
    // Elements
    const addPasswordBtn = document.getElementById('add-password-btn');
    const generatePasswordBtn = document.getElementById('generate-password-btn');
    const addModal = document.getElementById('add-modal');
    const viewModal = document.getElementById('view-modal');
    const closeButtons = document.querySelectorAll('.close-btn');
    const passwordForm = document.getElementById('password-form');
    const searchInput = document.getElementById('search-input');
    const passwordContainer = document.getElementById('password-container');
    const generatePasswordModal = document.getElementById('generate-modal');

    // Show add password modal
    addPasswordBtn.addEventListener('click', function () {
        addModal.classList.add('show');
    });

    // Show generate password modal
    generatePasswordBtn.addEventListener('click', function () {
        generatePasswordModal.classList.add('show');
        generateNewPassword();
    });

    // Close modals
    closeButtons.forEach(button => {
        button.addEventListener('click', function () {
            addModal.classList.remove('show');
            viewModal.classList.remove('show');
            generatePasswordModal.classList.remove('show');
        });
    });

    // Password form submission
    passwordForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const nameInput = document.getElementById('password-name');
        const usernameInput = document.getElementById('password-username');
        const passwordInput = document.getElementById('password-value');
        const websiteInput = document.getElementById('password-website');

        const passwordData = {
            name: nameInput.value,
            username: usernameInput.value,
            password: passwordInput.value,
            website: websiteInput.value
        };

        // Submit password to API
        fetch('/api/passwords', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(passwordData)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Failed to add password');
            })
            .then(data => {
                // Success - reload page to show new password
                window.location.reload();
            })
            .catch(error => {
                console.error('Error adding password:', error);
                alert('Failed to add password: ' + error.message);
            });
    });

    // Search functionality
    searchInput.addEventListener('input', function () {
        const searchTerm = this.value.toLowerCase();
        const passwordRows = document.querySelectorAll('.password-row');

        passwordRows.forEach(row => {
            const name = row.querySelector('.col.name').textContent.toLowerCase();
            const username = row.querySelector('.col.username').textContent.toLowerCase();

            if (name.includes(searchTerm) || username.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });

    // View password details
    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('view-btn') || e.target.closest('.view-btn')) {
            const button = e.target.classList.contains('view-btn') ? e.target : e.target.closest('.view-btn');
            const passwordId = button.dataset.id;

            // Get password details from API
            fetch(`/api/passwords/${passwordId}`)
                .then(response => response.json())
                .then(password => {
                    document.getElementById('view-name').textContent = password.name;
                    document.getElementById('view-username').textContent = password.username;
                    document.getElementById('view-password').value = password.password;
                    document.getElementById('view-website').textContent = password.website;
                    document.getElementById('view-strength').textContent = password.strength.displayName;
                    document.getElementById('view-strength').className = `strength-badge strength-${password.strength.name.toLowerCase()}`;

                    // Format dates
                    const createdDate = new Date(password.createdAt);
                    const expiresDate = new Date(password.expiresAt);

                    document.getElementById('view-created').textContent = createdDate.toLocaleDateString();
                    document.getElementById('view-expires').textContent = expiresDate.toLocaleDateString();

                    // Show modal
                    viewModal.classList.add('show');
                })
                .catch(error => {
                    console.error('Error fetching password:', error);
                    alert('Failed to retrieve password details');
                });
        }
    });

    // Copy password to clipboard
    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('copy-btn') || e.target.closest('.copy-btn')) {
            const button = e.target.classList.contains('copy-btn') ? e.target : e.target.closest('.copy-btn');
            const passwordText = button.dataset.password;

            navigator.clipboard.writeText(passwordText)
                .then(() => {
                    // Show success message
                    const originalHTML = button.innerHTML;
                    button.innerHTML = '<i class="fas fa-check"></i>';
                    setTimeout(() => {
                        button.innerHTML = originalHTML;
                    }, 2000);
                })
                .catch(err => {
                    console.error('Could not copy password: ', err);
                    alert('Failed to copy password');
                });
        }
    });

    // Delete password
    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('delete-btn') || e.target.closest('.delete-btn')) {
            if (!confirm('Are you sure you want to delete this password?')) {
                return;
            }

            const button = e.target.classList.contains('delete-btn') ? e.target : e.target.closest('.delete-btn');
            const passwordId = button.dataset.id;

            // Delete password from API
            fetch(`/api/passwords/${passwordId}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        // Remove the password row from the UI
                        const row = button.closest('.password-row');
                        row.remove();

                        // Update counts
                        updateCounts();
                    } else {
                        throw new Error('Failed to delete password');
                    }
                })

                .catch(error => {
                    console.error('Error deleting password:', error);
                    alert('Failed to delete password');
                });
        }}
);


    // Password toggle visibility
    const togglePasswordVisibility = function (inputElement, toggleElement) {
        if (inputElement.type === 'password') {
            inputElement.type = 'text';
            toggleElement.innerHTML = '<i class="fas fa-eye-slash"></i>';
        } else {
            inputElement.type = 'password';
            toggleElement.innerHTML = '<i class="fas fa-eye"></i>';
        }
    };

    document.querySelectorAll('.toggle-password').forEach(toggle => {
        toggle.addEventListener('click', function () {
            const input = this.previousElementSibling;
            togglePasswordVisibility(input, this);
        });
    });

    // Password strength meter
    const passwordInput = document.getElementById('password-value');
    const strengthMeter = document.getElementById('strength-meter');
    const strengthText = document.getElementById('strength-text');

    if (passwordInput) {
        passwordInput.addEventListener('input', function () {
            if (this.value.length === 0) {
                strengthMeter.style.width = '0%';
                strengthMeter.className = 'strength-meter-bar';
                strengthText.textContent = '';
                return;
            }

            // Check password strength via API
            fetch('/api/check-strength', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ password: this.value })
            })
                .then(response => response.json())
                .then(data => {
                    // Update strength meter
                    const level = data.level;
                    let percentage = (level / 3) * 100;
                    strengthMeter.style.width = `${percentage}%`;

                    // Update class and text
                    strengthMeter.className = 'strength-meter-bar';
                    switch (data.strength) {
                        case 'WEAK':
                            strengthMeter.classList.add('weak');
                            break;
                        case 'MEDIUM':
                            strengthMeter.classList.add('medium');
                            break;
                        case 'STRONG':
                            strengthMeter.classList.add('strong');
                            break;
                        case 'VERY_STRONG':
                            strengthMeter.classList.add('very-strong');
                            break;
                    }

                    strengthText.textContent = data.display;
                })
                .catch(error => {
                    console.error('Error checking password strength:', error);
                });
        });
    }

    // Password generator functionality
    const generateNewPassword = function () {
        const lengthSlider = document.getElementById('password-length');
        const specialChars = document.getElementById('special-chars');
        const generatedPassword = document.getElementById('generated-password');

        const length = lengthSlider.value;
        const includeSpecial = specialChars.checked;

        // Generate password via API
        fetch(`/api/generate-password?length=${length}&special=${includeSpecial}`)
            .then(response => response.json())
            .then(data => {
                generatedPassword.value = data.password;
            })
            .catch(error => {
                console.error('Error generating password:', error);
                alert('Failed to generate password');
            });
    };

    // Update generated password when settings change
    document.getElementById('password-length').addEventListener('input', function () {
        document.getElementById('length-value').textContent = this.value;
        generateNewPassword();
    });

    document.getElementById('special-chars').addEventListener('change', generateNewPassword);

    // Copy generated password
    document.getElementById('copy-generated-password').addEventListener('click', function () {
        const generatedPassword = document.getElementById('generated-password');
        navigator.clipboard.writeText(generatedPassword.value)
            .then(() => {
                // Show success message
                const originalText = this.innerHTML;
                this.innerHTML = '<i class="fas fa-check"></i> Copied!';
                setTimeout(() => {
                    this.innerHTML = originalText;
                }, 2000);
            })
            .catch(err => {
                console.error('Could not copy password: ', err);
                alert('Failed to copy password');
            });
    });

    // Use generated password
    document.getElementById('use-generated-password').addEventListener('click', function () {
        const generatedPassword = document.getElementById('generated-password').value;
        document.getElementById('password-value').value = generatedPassword;

        // Trigger the strength check
        document.getElementById('password-value').dispatchEvent(new Event('input'));

        // Close generator modal and return to add password
        generatePasswordModal.classList.remove('show');
    });

    // Update counts after actions
    function updateCounts() {
        fetch('/api/passwords')
            .then(response => response.json())
            .then(passwords => {
                // Count weak and strong passwords
                let weakCount = 0;
                let strongCount = 0;
                let expiringCount = 0;

                passwords.forEach(password => {
                    if (password.strength === 'WEAK') {
                        weakCount++;
                    } else if (password.strength === 'STRONG' || password.strength === 'VERY_STRONG') {
                        strongCount++;
                    }

                    const expiresDate = new Date(password.expiresAt);
                    const now = new Date();
                    const oneWeek = new Date();
                    oneWeek.setDate(oneWeek.getDate() + 7);

                    if (expiresDate <= oneWeek) {
                        expiringCount++;
                    }
                });

                // Update UI counts
                document.getElementById('weak-count').textContent = weakCount;
                document.getElementById('strong-count').textContent = strongCount;
                document.getElementById('expiring-count').textContent = expiringCount;
            })
            .catch(error => {
                console.error('Error fetching passwords for count update:', error);
            });
    }
    // Ajouter le gestionnaire d'événements pour le bouton de citation
    const citationBtn = document.getElementById('citation-btn');
    if (citationBtn) {
        citationBtn.addEventListener('click', function() {
            // Rediriger vers le service de citation
                window.location.href = 'http://citation-service:8081';
            });
                    
        
    }
})