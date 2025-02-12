const { GoogleAuth } = require('google-auth-library');
const { IAMCredentialsClient } = require('@google-cloud/iam-credentials');

exports.rotateServiceAccountKeys = async (req, res) => {
  try {
    const auth = new GoogleAuth({
      scopes: ['https://www.googleapis.com/auth/cloud-platform']
    });

    const client = new IAMCredentialsClient();
    const projectId = await auth.getProjectId();

    // List all service accounts
    const [serviceAccounts] = await client.listServiceAccounts({
      name: `projects/${projectId}`
    });

    for (const account of serviceAccounts) {
      // List existing keys
      const [keys] = await client.listServiceAccountKeys({
        name: account.name
      });

      // Sort keys by creation time
      keys.sort((a, b) => {
        return new Date(b.validAfterTime) - new Date(a.validAfterTime);
      });

      // Create new key
      const [newKey] = await client.createServiceAccountKey({
        name: account.name
      });

      // Delete oldest key if more than 2 exist
      if (keys.length > 1) {
        await client.deleteServiceAccountKey({
          name: keys[keys.length - 1].name
        });
      }

      console.log(`Rotated keys for ${account.email}`);
    }

    res.status(200).send('Service account keys rotated successfully');
  } catch (error) {
    console.error('Error rotating service account keys:', error);
    res.status(500).send('Error rotating service account keys');
  }
};
