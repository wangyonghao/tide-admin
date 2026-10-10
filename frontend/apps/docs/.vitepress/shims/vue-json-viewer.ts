import { defineComponent, h } from 'vue';

/**
 * `@vben/common-ui` 的桶会带上 JsonViewer，而 `vue-json-viewer` 在加载时读取 window。
 * 文档页并不渲染这个查看器；用空组件让 VitePress 的 SSR 能跑完。
 */
export default defineComponent({
  name: 'VueJsonViewer',
  setup(_, { slots }) {
    return () => h('pre', slots.default?.());
  },
});
