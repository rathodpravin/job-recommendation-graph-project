import { useState } from 'react';

function App() {
  const [activeTab, setActiveTab] = useState('recommendations'); // 'recommendations' | 'allJobs' | 'allUsers' | 'addUser'
  const [userIdInput, setUserIdInput] = useState('u101');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Form State
  const [newUserId, setNewUserId] = useState('');
  const [newUserName, setNewUserName] = useState('');
  const [newUserSkills, setNewUserSkills] = useState('');
  const [formSuccess, setFormSuccess] = useState('');

  const API_BASE_URL = 'http://localhost:8080/api/v1/recommendations';

  const fetchData = async (tab, targetUserId = userIdInput) => {
    setActiveTab(tab);
    setLoading(true);
    setError(null);
    setFormSuccess('');
    setData([]);

    let endpoint = `${API_BASE_URL}/jobs`;
    if (tab === 'recommendations') endpoint = `${API_BASE_URL}/jobs/${targetUserId}`;
    if (tab === 'allUsers') endpoint = `${API_BASE_URL}/users`;

    try {
      const response = await fetch(endpoint);
      if (!response.ok) throw new Error(`HTTP Error! Status: ${response.status}`);
      const json = await response.json();
      if (Array.isArray(json)) setData(json);
      else throw new Error("Invalid response format received from server");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setFormSuccess('');

    const skillsArray = newUserSkills.split(',').map(s => s.trim()).filter(Boolean);

    try {
      const response = await fetch(`${API_BASE_URL}/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          id: newUserId,
          name: newUserName,
          skills: skillsArray
        })
      });

      if (!response.ok) throw new Error('Failed to create user node');
      
      setFormSuccess(`User '${newUserName}' created! Search recommendations for ID: ${newUserId}`);
      setUserIdInput(newUserId);
      setNewUserId('');
      setNewUserName('');
      setNewUserSkills('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-card" style={{ maxWidth: '800px', margin: '40px auto' }}>
      <div className="header">
        <div>
          <h2 style={{ color: '#60a5fa' }}>
            <i className="fa-solid fa-diagram-project"></i> Graph Job Matcher
          </h2>
          <p style={{ fontSize: '12px', color: '#94a3b8', marginTop: '4px' }}>
            Enterprise Graph Matcher connected to Spring Boot & CognoDB
          </p>
        </div>
        <span className="badge-status">
          <i className="fa-solid fa-signal"></i> Connected
        </span>
      </div>

      {/* Navigation Tabs */}
      <div className="tab-group" style={{ display: 'flex', gap: '8px', marginBottom: '20px' }}>
        <button 
          className={`tab-btn ${activeTab === 'recommendations' ? 'active' : ''}`}
          onClick={() => fetchData('recommendations')}
        >
          Recommendations
        </button>
        <button 
          className={`tab-btn ${activeTab === 'allJobs' ? 'active' : ''}`}
          onClick={() => fetchData('allJobs')}
        >
          All Jobs
        </button>
        <button 
          className={`tab-btn ${activeTab === 'allUsers' ? 'active' : ''}`}
          onClick={() => fetchData('allUsers')}
        >
          All Users
        </button>
        <button 
          className={`tab-btn ${activeTab === 'addUser' ? 'active' : ''}`}
          onClick={() => { setActiveTab('addUser'); setError(null); setFormSuccess(''); }}
        >
          + Add User
        </button>
      </div>

      {/* Recommendations ID Input */}
      {activeTab === 'recommendations' && (
        <div style={{ marginBottom: '16px', display: 'flex', gap: '8px', alignItems: 'center' }}>
          <label style={{ fontSize: '13px', color: '#94a3b8' }}>User ID:</label>
          <input 
            type="text" 
            value={userIdInput} 
            onChange={(e) => setUserIdInput(e.target.value)}
            style={{
              background: '#0f172a', border: '1px solid #334155', color: '#fff',
              padding: '8px 12px', borderRadius: '6px', fontSize: '13px', width: '120px'
            }}
          />
          <button className="action-btn" style={{ width: 'auto', padding: '8px 16px' }} onClick={() => fetchData('recommendations')}>
            Fetch Match Results
          </button>
        </div>
      )}

      {/* Add User Form */}
      {activeTab === 'addUser' && (
        <form onSubmit={handleCreateUser} style={{ display: 'flex', flexDirection: 'column', gap: '12px', background: '#1e293b', padding: '16px', borderRadius: '8px' }}>
          <div>
            <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>User ID</label>
            <input 
              required placeholder="e.g. u105" value={newUserId} 
              onChange={(e) => setNewUserId(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #334155', background: '#0f172a', color: '#fff' }}
            />
          </div>
          <div>
            <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Full Name</label>
            <input 
              required placeholder="e.g. Bob" value={newUserName} 
              onChange={(e) => setNewUserName(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #334155', background: '#0f172a', color: '#fff' }}
            />
          </div>
          <div>
            <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Skills (comma separated)</label>
            <input 
              required placeholder="e.g. Java, Neo4j" value={newUserSkills} 
              onChange={(e) => setNewUserSkills(e.target.value)}
              style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #334155', background: '#0f172a', color: '#fff' }}
            />
          </div>
          <button className="action-btn" type="submit" disabled={loading} style={{ marginTop: '8px' }}>
            {loading ? 'Saving Node...' : '+ Add Node to CognoDB'}
          </button>
        </form>
      )}

      {/* Dynamic Results Display */}
      <div className="results-area" style={{ marginTop: '20px' }}>
        {formSuccess && (
          <div style={{ color: '#34d399', background: 'rgba(16, 185, 129, 0.1)', padding: '12px', borderRadius: '8px', fontSize: '14px', marginBottom: '12px' }}>
            <i className="fa-solid fa-circle-check"></i> {formSuccess}
          </div>
        )}

        {error && (
          <div style={{ color: '#f87171', background: 'rgba(239, 68, 68, 0.1)', padding: '12px', borderRadius: '8px', fontSize: '14px', marginBottom: '12px' }}>
            <i className="fa-solid fa-triangle-exclamation"></i> Error: {error}
          </div>
        )}

        {/* Empty State Message */}
        {!loading && data.length === 0 && !error && activeTab === 'recommendations' && (
          <div style={{ textAlign: 'center', background: 'rgba(51, 65, 85, 0.3)', border: '1px solid #334155', borderRadius: '10px', padding: '20px', color: '#94a3b8', fontSize: '14px' }}>
            <i className="fa-solid fa-user-slash" style={{ fontSize: '24px', marginBottom: '8px', display: 'block', color: '#64748b' }}></i>
            No recommended job roles found for User ID <strong style={{ color: '#60a5fa' }}>{userIdInput}</strong>.
          </div>
        )}

        {/* Match Breakdown Cards */}
        {activeTab === 'recommendations' && data.map((item) => (
          <div className="data-card" key={item.jobId} style={{ display: 'flex', flexDirection: 'column', gap: '8px', padding: '14px', background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', marginBottom: '10px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
              <div>
                <h4 style={{ fontSize: '16px', fontWeight: '600', color: '#f8fafc' }}>{item.jobTitle}</h4>
                <p style={{ fontSize: '12px', color: '#94a3b8' }}>Job ID: {item.jobId}</p>
              </div>
              <span style={{ fontSize: '14px', fontWeight: 'bold', background: 'rgba(16, 185, 129, 0.15)', color: '#34d399', border: '1px solid #059669', padding: '4px 10px', borderRadius: '20px' }}>
                {item.matchPercentage}% Match
              </span>
            </div>
            <div style={{ fontSize: '12px', color: '#cbd5e1' }}>
              <strong style={{ color: '#38bdf8' }}>Matched Skills:</strong> {item.matchedSkills?.join(', ')}
            </div>
            {item.missingSkills && item.missingSkills.length > 0 && (
              <div style={{ fontSize: '12px', color: '#f87171' }}>
                <strong>Skill Gap:</strong> {item.missingSkills.join(', ')}
              </div>
            )}
          </div>
        ))}

        {/* All Jobs Cards */}
        {activeTab === 'allJobs' && data.map((item) => (
          <div className="data-card" key={item.jobId} style={{ padding: '14px', background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h4 style={{ fontSize: '16px', fontWeight: '600', color: '#f8fafc' }}>{item.jobTitle}</h4>
              <p style={{ fontSize: '12px', color: '#94a3b8' }}>Job ID: {item.jobId}</p>
            </div>
            <span style={{ fontSize: '12px', color: '#93c5fd', background: '#1e3a8a', padding: '4px 8px', borderRadius: '4px' }}>
              {item.requiredSkills ? item.requiredSkills.join(', ') : 'Open Position'}
            </span>
          </div>
        ))}

        {/* All Users Cards */}
        {activeTab === 'allUsers' && data.map((user) => (
          <div className="data-card" key={user.userId} style={{ padding: '14px', background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h4 style={{ fontSize: '16px', fontWeight: '600', color: '#f8fafc' }}>{user.userName}</h4>
              <p style={{ fontSize: '12px', color: '#94a3b8' }}>User ID: {user.userId}</p>
            </div>
            <span style={{ fontSize: '12px', color: '#34d399', background: '#064e3b', padding: '4px 8px', borderRadius: '4px' }}>
              {user.skills && user.skills.length > 0 ? user.skills.join(', ') : 'No skills listed'}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;