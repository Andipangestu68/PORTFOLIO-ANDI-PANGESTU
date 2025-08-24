#!/usr/bin/env python
# coding: utf-8

# # Random Forest Regression

# ## Importing the libraries

# In[1]:


import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


# ## Importing the dataset

# In[49]:


dataset = pd.read_csv('Salary Data.csv')
dataset.info()


# In[15]:


dataset.isnull().any()


# In[50]:


dataset = dataset.dropna()
dataset


# In[51]:


from sklearn.preprocessing import LabelEncoder
label_encoder = LabelEncoder()
dataset['Gender'] = label_encoder.fit_transform(dataset['Gender'])
dataset['Education Level'] = label_encoder.fit_transform(dataset['Education Level'])
dataset['Job Title'] = label_encoder.fit_transform(dataset['Job Title'])
dataset


# In[52]:


dataset['Age'] = dataset['Age'].astype(int)
dataset['Salary'] = dataset['Salary'].astype(int)
dataset.describe()


# In[53]:


X = np.array(dataset.iloc[:,:-1])
y = np.array(dataset.iloc[:,-1])


# ## Splitting dataset into training set and test set

# In[54]:


from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=0 )


# ## Feature Scalling

# In[55]:


from sklearn.preprocessing import StandardScaler
sc = StandardScaler()
X_train = sc.fit_transform(X_train)
X_test = sc.transform(X_test)


# In[56]:


X_train


# # Logistic Regression

# In[28]:


from sklearn.linear_model import LogisticRegression
regressor = LogisticRegression(random_state=0)
regressor.fit(X_train, y_train)


# In[29]:


y_regressor = regressor.predict(X_test)
print(np.concatenate((y_regressor.reshape(len(y_regressor), 1), y_test.reshape(len(y_test), 1)), 1))


# # Multiple Linear Regression

# In[68]:


from sklearn.linear_model import LinearRegression
regressor_multiple = LinearRegression()
regressor_multiple.fit(X_train, y_train)


# In[69]:


y_regressor_multiple = regressor_multiple.predict(X_test)
np.set_printoptions(precision=2)
print(np.concatenate((y_regressor_multiple.reshape(len(y_regressor_multiple), 1), y_test.reshape(len(y_test), 1)), 1))


# ## Training the Decision Tree Regression model on the Training set

# In[43]:


from sklearn.tree import DecisionTreeRegressor
dt = DecisionTreeRegressor(random_state=0)
dt.fit(X_train,y_train)


# In[44]:


y_dt = dt.predict(X_test)
np.set_printoptions(precision=2)
print(np.concatenate((y_dt.reshape(len(y_dt), 1), y_test.reshape(len(y_test), 1)), 1))


# ## Training the Random Forest Regression model on the Training set

# In[71]:


from sklearn.ensemble import RandomForestRegressor
rf = RandomForestRegressor(n_estimators=10, random_state=0)
rf.fit(X_train,y_train)


# ## Predicting the test set results

# In[72]:


y_rf = rf.predict(X_test)
np.set_printoptions(precision=2)
print(np.concatenate((y_rf.reshape(len(y_rf), 1), y_test.reshape(len(y_test), 1)), 1))


# ## Training the SVR Regression model on the Training set

# In[45]:


from sklearn.svm import SVR
svr = SVR(kernel='rbf')
#X,y disini adalah X,y yang sudah mengalami transformasi scalling
svr.fit(X_train,y_train)


# In[58]:


y_svr = svr.predict(X_test)
np.set_printoptions(precision=2)
print(np.concatenate((y_svr.reshape(len(y_svr), 1), y_test.reshape(len(y_test), 1)), 1))


# ## Training the Polynomial Linear Regression model on the Training set

# In[59]:


from sklearn.linear_model import LinearRegression
from sklearn.preprocessing import PolynomialFeatures
poly_reg = PolynomialFeatures(degree=4)
X_poly = poly_reg.fit_transform(X_train)
polynomial = LinearRegression()
polynomial.fit(X_poly, y_train)


# In[60]:


y_poly = polynomial.predict(poly_reg.transform(X_test))
np.set_printoptions(precision=2)
print(np.concatenate((y_poly.reshape(len(y_poly), 1), y_test.reshape(len(y_test), 1)), 1))


# ## Evaluation

# In[62]:


from sklearn.metrics import r2_score
r2_score(y_test, y_regressor)


# In[73]:


#random forest
r2_score(y_test, y_rf)


# In[63]:


#decision tree
r2_score(y_test, y_dt)


# In[70]:


#multiple linear
r2_score(y_test, y_regressor_multiple)


# In[65]:


#svr
r2_score(y_test, y_svr)


# In[66]:


#polynomial
r2_score(y_test, y_poly)

print('accuracy score : ', r2_score(y_test, y_rf))
