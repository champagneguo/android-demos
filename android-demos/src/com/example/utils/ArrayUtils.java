package com.example.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class ArrayUtils {

	/**
	 * ��������Ĺ�����(��int������Ϊ��)
	 */
	///////////////////////////////////////////////
	//�����㷨��ʼ
	//////////////////////////////////////////////
	/**
	* �����㷨�ķ������£�
	* 1.��������ֱ�Ӳ��������۰��������ϣ�����򣩣�
	* 2.��������ð�������򡢿������򣩣�
	* 3.ѡ������ֱ��ѡ�����򡢶����򣩣� 
	* 4.�鲢���� 
	* 5.��������
	*
	* �������򷽷���ѡ��
	* (1)��n��С(��n��50)���ɲ���ֱ�Ӳ����ֱ��ѡ������
	* (2)���ļ���ʼ״̬��������(ָ����)����Ӧѡ��ֱ�Ӳ��ˡ�ð�ݻ�����Ŀ�������Ϊ�ˣ�
	* (3)��n�ϴ���Ӧ����ʱ�临�Ӷ�ΪO(nlgn)�����򷽷����������򡢶������鲢����
	*
	*/
	
	 /**
     * ������������Ԫ��
     *
     * @since 1.1
     * @param ints
     *            ��Ҫ���н�������������
     * @param x
     *            �����е�λ��1
     * @param y
     *            �����е�λ��2
     * @return �����������
     */
    public static int[] swap(int[] ints, int x, int y) {
        int temp = ints[x];
        ints[x] = ints[y];
        ints[y] = temp;
        return ints;
    }

    /**
     * ð������ ������������Ԫ�ؽ��бȽ� ���ܣ��Ƚϴ���O(n^2),n^2/2����������O(n^2),n^2/4
     *
     * @since 1.1
     * @param source
     *            ��Ҫ�����������������
     * @return ����������
     */
    public static int[] bubbleSort(int[] source) {
        for (int i = 1; i < source.length; i++) {
            for (int j = 0; j < i; j++) {
                if (source[j] > source[j + 1]) {
                    swap(source, j, j + 1);
                }
            }
        }
        return source;
    }

    /**
     * ֱ��ѡ������ ������ÿһ�˴Ӵ����������Ԫ����ѡ����С������󣩵�һ��Ԫ�أ� ˳��������ź�������е����ֱ��ȫ�������������Ԫ�����ꡣ
     * ���ܣ��Ƚϴ���O(n^2),n^2/2 ��������O(n),n
     * ����������ð�������ٶ��ˣ����ڽ�������CPUʱ��ȱȽ������CUPʱ��࣬����ѡ�������ð������졣
     * ����N�Ƚϴ�ʱ���Ƚ������CPUʱ��ռ��Ҫ��λ��������ʱ�����ܺ�ð������̫�࣬���������ʿ϶�Ҫ��Щ��
     *
     * @since 1.1
     * @param source
     *            ��Ҫ�����������������
     * @return ����������
     */
    public static int[] selectSort(int[] source) {

        for (int i = 0; i < source.length; i++) {
            for (int j = i + 1; j < source.length; j++) {
                if (source[i] > source[j]) {
                    swap(source, i, j);
                }
            }
        }
        return source;
    }

    /**
     * �������� ��������һ����¼���뵽���ź����������п����ǿձ���,�Ӷ��õ�һ���µļ�¼����1������� ���ܣ��Ƚϴ���O(n^2),n^2/4
     * ���ƴ���O(n),n^2/4 �Ƚϴ�����ǰ���ߵ�һ�㣬�����������CPUʱ��Ͻ����٣����������ϱ�ð���������һ���࣬����ѡ������ҲҪ�졣
     *
     * @since 1.1
     * @param source
     *            ��Ҫ�����������������
     * @return ����������
     */
    public static int[] insertSort(int[] source) {

        for (int i = 1; i < source.length; i++) {
            for (int j = i; (j > 0) && (source[j] < source[j - 1]); j--) {
                swap(source, j, j - 1);
            }
        }
        return source;
    }

    /**
     * �������� ��������ʹ�÷��η���Divide and conquer����������һ�����У�list����Ϊ���������У�sub-lists���� ����Ϊ��
     * 1. ������������һ��Ԫ�أ���Ϊ "��׼"��pivot���� 2.
     * �����������У�����Ԫ�رȻ�׼ֵС�İڷ��ڻ�׼ǰ�棬����Ԫ�رȻ�׼ֵ��İ��ڻ�׼�ĺ���
     * ����ͬ�������Ե���һ�ߣ���������ָ�֮�󣬸û�׼���������λ�á������Ϊ�ָpartition�������� 3.
     * �ݹ�أ�recursive����С�ڻ�׼ֵԪ�ص������кʹ��ڻ�׼ֵԪ�ص�����������
     * �ݻص���ײ����Σ������еĴ�С�����һ��Ҳ������Զ���Ѿ����������
     * ����Ȼһֱ�ݻ���ȥ����������㷨�ܻ��������Ϊ��ÿ�εĵ�����iteration���У������ٻ��һ��Ԫ�ذڵ�������λ��ȥ��
     *
     * @since 1.1
     * @param source
     *            ��Ҫ�����������������
     * @return ����������
     */
    public static int[] quickSort(int[] source) {
        return qsort(source, 0, source.length - 1);
    }

    /**
     * ��������ľ���ʵ�֣�������
     *
     * @since 1.1
     * @param source
     *            ��Ҫ�����������������
     * @param low
     *            ��ʼ��λ
     * @param high
     *            ������λ
     * @return ����������
     */
    private static int[] qsort(int source[], int low, int high) {
        int i, j, x;
        if (low < high) {
            i = low;
            j = high;
            x = source[i];
            while (i < j) {
                while (i < j && source[j] > x) {
                    j--;
                }
                if (i < j) {
                    source[i] = source[j];
                    i++;
                }
                while (i < j && source[i] < x) {
                    i++;
                }
                if (i < j) {
                    source[j] = source[i];
                    j--;
                }
            }
            source[i] = x;
            qsort(source, low, i - 1);
            qsort(source, i + 1, high);
        }
        return source;
    }
    ///////////////////////////////////////////////
	//�����㷨����
	//////////////////////////////////////////////
    /**
     * ���ַ����� �������Ա�����������б�
     *
     * @since 1.1
     * @param source
     *            ��Ҫ���в��Ҳ���������
     * @param key
     *            ��Ҫ���ҵ�ֵ
     * @return ��Ҫ���ҵ�ֵ�������е�λ�ã���δ�鵽�򷵻�-1
     */
    public static int binarySearch(int[] source, int key) {
        int low = 0, high = source.length - 1, mid;
        while (low <= high) {
            mid = (low + high) >>> 1;
            if (key == source[mid]) {
                return mid;
            } else if (key < source[mid]) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    /**
     * ��ת����
     *
     * @since 1.1
     * @param source
     *            ��Ҫ���з�ת����������
     * @return ��ת�������
     */
    public static int[] reverse(int[] source) {
        int length = source.length;
        int temp = 0;
        for (int i = 0; i < length>>1; i++) {
            temp = source[i];
            source[i] = source[length - 1 - i];
            source[length - 1 - i] = temp;
        }
        return source;
    }
   /**
    * �ڵ�ǰλ�ò���һ��Ԫ��,������ԭ��Ԫ������ƶ�;
	* �������λ�ó���ԭ���飬����IllegalArgumentException�쳣
    * @param array
    * @param index
    * @param insertNumber
    * @return
    */
	public static int[] insert(int[] array, int index, int insertNumber) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException();
		}
		if (index-1 > array.length || index <= 0) {
			throw new IllegalArgumentException();
		}
		int[] dest=new int[array.length+1];
		System.arraycopy(array, 0, dest, 0, index-1);
		dest[index-1]=insertNumber;
		System.arraycopy(array, index-1, dest, index, dest.length-index);
		return dest;
	}
	
	/**
	 * �����������ض�λ��ɾ����һ��Ԫ��,������ԭ��Ԫ����ǰ�ƶ�;
	 * �������λ�ó���ԭ���飬����IllegalArgumentException�쳣
	 * @param array
	 * @param index
	 * @return
	 */
	public static int[] remove(int[] array, int index) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException();
		}
		if (index > array.length || index <= 0) {
			throw new IllegalArgumentException();
		}
		int[] dest=new int[array.length-1];
		System.arraycopy(array, 0, dest, 0, index-1);
		System.arraycopy(array, index, dest, index-1, array.length-index);
		return dest;
	}
	/**
	 * 2������ϲ����γ�һ���µ�����
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static int[] merge(int[] array1,int[] array2) {
		int[] dest=new int[array1.length+array2.length];
		System.arraycopy(array1, 0, dest, 0, array1.length);
		System.arraycopy(array2, 0, dest, array1.length, array2.length);
		return dest;
	}

/**
	 * ��������n�����ݣ�Ҫ������˳��ѭ������ƶ�kλ��
	 * ��ǰ���Ԫ������ƶ�kλ�������Ԫ����ѭ����ǰ��kλ��
	 * ���磬0��1��2��3��4ѭ���ƶ�3λ��Ϊ2��3��4��0��1��
	 * @param array
	 * @param offset
	 * @return
	 */
	public static int[] offsetArray(int[] array,int offset){
		int length = array.length;  
		int moveLength = length - offset; 
		int[] temp = Arrays.copyOfRange(array, moveLength, length);
		System.arraycopy(array, 0, array, offset, moveLength);  
		System.arraycopy(temp, 0, array, 0, offset);
		return array;
	}
	/**
	 * �������һ������
	 * @param list
	 * @return
	 */
	public static List<?> shuffle(List<?> list){
		java.util.Collections.shuffle(list);
		return list;
	}

	/**
	 * �������һ������
	 * @param array
	 * @return
	 */
	public int[] shuffle(int[] array) {
		Random random = new Random();
		for (int index = array.length - 1; index >= 0; index--) {
			// ��0��index��֮�����ȡһ��ֵ����index����Ԫ�ؽ���
			exchange(array, random.nextInt(index + 1), index);
		}
		return array;
	}

	// ����λ��
	private void exchange(int[] array, int p1, int p2) {
		int temp = array[p1];
		array[p1] = array[p2];
		array[p2] = temp;
	}
/**
	 * ����������������кϲ�,�����ظ������ֽ���ȥ��
	 * 
	 * @param a�����ź��������a
	 * @param b�����ź��������b
	 * @return �ϲ������������
	 */
	private static List<Integer> mergeByList(int[] a, int[] b) {
		// ���ڷ��ص������飬���ȿ��ܲ�Ϊa,b����֮�ͣ���Ϊ�������ظ���������Ҫȥ��
		List<Integer> c = new ArrayList<Integer>();
		// a�����±�
		int aIndex = 0;
		// b�����±�
		int bIndex = 0;
		// ��a��b�������ֵ���бȽϣ�����С��ֵ�ӵ�c�������������±�+1��
		// �����ȣ���������һ���ӵ�c���������±��+1
		// ����±곬�������鳤�ȣ����˳�ѭ��
		while (true) {
			if (aIndex > a.length - 1 || bIndex > b.length - 1) {
				break;
			}
			if (a[aIndex] < b[bIndex]) {
				c.add(a[aIndex]);
				aIndex++;
			} else if (a[aIndex] > b[bIndex]) {
				c.add(b[bIndex]);
				bIndex++;
			} else {
				c.add(a[aIndex]);
				aIndex++;
				bIndex++;
			}
		}
		// ��û�г��������±����������ȫ���ӵ�����c��
		// ���a���黹������û�д���
		if (aIndex <= a.length - 1) {
			for (int i = aIndex; i <= a.length - 1; i++) {
				c.add(a[i]);
			}
			// ���b�����л�������û�д���
		} else if (bIndex <= b.length - 1) {
			for (int i = bIndex; i <= b.length - 1; i++) {
				c.add(b[i]);
			}
		}
		return c;
	}
	/**
	 * ����������������кϲ�,�����ظ������ֽ���ȥ��
	 * @param a:���ź��������a
	 * @param b:���ź��������b
	 * @return�ϲ������������,��������ĳ���=a.length + b.length,���㲿�ֲ�0
	 */
	private static int[] mergeByArray(int[] a, int[] b){
		int[] c = new int[a.length + b.length];

		int i = 0, j = 0, k = 0;

		while (i < a.length && j < b.length) {
			if (a[i] <= b[j]) {
				if (a[i] == b[j]) {
					j++;
				} else {
					c[k] = a[i];
					i++;
					k++;
				}
			} else {
				c[k] = b[j];
				j++;
				k++;
			}
		}
		while (i < a.length) {
			c[k] = a[i];
			k++;
			i++;
		}
		while (j < b.length) {
			c[k] = b[j];
			j++;
			k++;
		}
		return c;
	}
	/**
	 * ����������������кϲ�,�����ظ������ֽ���ȥ��
	 * @param a��������û�����������
	 * @param b��������û�����������
	 * @return�ϲ������������
	 * ��ӡʱ����������
	 * Map<Integer,Integer> map=sortByTreeMap(a,b);
		Iterator iterator =  map.entrySet().iterator();   
		while (iterator.hasNext()) {   
           Map.Entry mapentry = (Map.Entry)iterator.next();   
           System.out.print(mapentry.getValue()+" ");   
		}
	 */
	private static Map<Integer,Integer> mergeByTreeMap(int[] a, int[] b) {
		Map<Integer,Integer> map=new TreeMap<Integer,Integer>();
		for(int i=0;i<a.length;i++){
			map.put(a[i], a[i]);
		}
		for(int i=0;i<b.length;i++){
			map.put(b[i], b[i]);
		}
		return map;
	}
	/**
	 * �ڿ���̨��ӡ���飬֮���ö��Ÿ���,����ʱ��
	 * @param array
	 */
	public static String print(int[] array){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<array.length;i++){
			sb.append(","+array[i]);
		}
		System.out.println(sb.toString().substring(1));
		return sb.toString().substring(1);
	}
	
	public static int indexOf(byte key, byte[] array, int start, int length) {
		int pos = -1;
		if (start < 0) start = 0;
		int end = Math.min(array.length-start, length) + start;
		if (end < start) end = start;
		for (int i=start; i<end; i++) {
			if (array[i] == key) {
				pos = i;
				break;
			}
		}
		return pos;
	}
	
	public static int indexOf(byte key, byte[] array, int start) {
		return indexOf(key, array, start, array.length);
	}
	
	public static int indexOf(byte key, byte[] array) {
		return indexOf(key, array, 0);
	}
	
	public static int count(byte key, byte[] array, int start, int length) {
		int count = 0;
		if (start < 0) start = 0;
		int end = Math.min(array.length-start, length) + start;
		if (end < start) end = start;
		for (int i=start; i<end; i++) {
			if (array[i] == key) {
				count++;
			}
		}
		return count;
	}
	
	public static int count(byte key, byte[] array, int start) {
		return count(key, array, start, array.length);
	}
	
	public static int count(byte key, byte[] array) {
		return count(key, array, 0);
	}
}

