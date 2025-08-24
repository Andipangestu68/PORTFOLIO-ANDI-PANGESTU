import numpy as np
import pandas as pd
import matplotlib

print("Numpy version:", np.__version__)
print("Pandas version:", pd.__version__)
print("Matplotlib version:", matplotlib.__version__)

# Coba membuat plot sederhana untuk memastikan Matplotlib berfungsi
import matplotlib.pyplot as plt

plt.plot([1, 2, 3], [4, 5, 6])
plt.title('Test Plot')
plt.show()
